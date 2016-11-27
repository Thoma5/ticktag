package io.ticktag.service.comment.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.loggedtime.LoggedTimeRepository
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.AssignedTicketUserKey
import io.ticktag.persistence.ticket.entity.LoggedTime
import io.ticktag.persistence.ticketassignment.TicketAssignmentRepository
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.*
import io.ticktag.service.comment.dto.CommentCommand
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment

import io.ticktag.service.loggedtime.dto.CreateLoggedTime

import io.ticktag.service.comment.dto.UpdateComment
import io.ticktag.service.comment.service.CommentService
import io.ticktag.service.loggedtime.service.LoggedTimeService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class CommentServiceImpl @Inject constructor(
        private val comments: CommentRepository,
        private val tickets: TicketRepository,
        private val users: UserRepository,
        private val loggedTimeService: LoggedTimeService,
        private val loggedTimes: LoggedTimeRepository,
        private val ticketTags: TicketTagRepository,
        private val timeCategories: TimeCategoryRepository,
        private val assignmentTags: AssignmentTagRepository,
        private val ticketAssignments: TicketAssignmentRepository
) : CommentService {

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun listCommentsForTicket(@P("authTicketId") ticketId: UUID): List<CommentResult> {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        return ticket.comments.filter { c -> c.describedTicket == null }.map(::CommentResult)
    }

    @PreAuthorize(AuthExpr.CREATE_COMMENT)
    override fun createComment(@Valid createComment: CreateComment, principal: Principal, @P("authTicketId") ticketId: UUID): CommentResult {
        val text = createComment.text
        val ticket = tickets.findOne(createComment.ticketId) ?: throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime, text, user, ticket)
        comments.insert(newComment)

        val errors = mutableListOf<ValidationError>()

        // TODO change events !!!!!!
        for ((index, command) in createComment.commands.withIndex()) {
            when (command) {
                is CommentCommand.Assign -> {
                    val assignUser = users.findOne(command.user)
                    val assignTag = assignmentTags.findOne(command.tag)
                    if (assignUser != null && assignTag != null) {
                        if (ticket.assignedTicketUsers.find { it.tag == assignTag && it.user == assignUser } == null) {
                            ticketAssignments.insert(AssignedTicketUser.create(ticket, assignTag, assignUser))
                        }
                    } else {
                        errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
                    }
                }
                is CommentCommand.Unassign -> {
                    val removeUser = users.findOne(command.user)
                    if (removeUser != null) {
                        if (command.tag == null) {
                            ticketAssignments.deleteByUserIdAndTicketId(removeUser.id, ticket.id)
                        } else {
                            val removeTag = assignmentTags.findOne(command.tag)
                            if (removeTag != null) {
                                val assignment = ticketAssignments.findOne(AssignedTicketUserKey.create(ticket, removeTag, removeUser))
                                if (assignment != null) {
                                    ticketAssignments.delete(assignment)
                                }
                            } else {
                                errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
                            }
                        }
                    } else {
                        errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
                    }
                }
                is CommentCommand.Close -> {
                    ticket.open = false
                }
                is CommentCommand.Reopen -> {
                    ticket.open = true
                }
                is CommentCommand.Tag -> {
                    val tag = ticketTags.findOne(command.tag)
                    if (tag != null) {
                        if (!ticket.tags.contains(tag)) {
                            ticket.tags.add(tag)
                        }
                    } else {
                        errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
                    }
                }
                is CommentCommand.Untag -> {
                    val tag = ticketTags.findOne(command.tag)
                    if (tag != null) {
                        if (ticket.tags.contains(tag)) {
                            ticket.tags.remove(tag)
                        }
                    } else {
                        errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
                    }
                }
                is CommentCommand.Est -> {
                    ticket.currentEstimatedTime = command.duration
                }
                is CommentCommand.Time -> {
                    val cat = timeCategories.findOne(command.category)
                    if (cat != null) {
                        loggedTimes.insert(LoggedTime.create(command.duration, newComment, cat))
                    } else {
                        errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
                    }
                }
            }
        }

        return CommentResult(newComment)
    }

    @PreAuthorize(AuthExpr.READ_COMMENT)
    override fun getComment(@P("authCommentId") commentId: UUID): CommentResult? {
        val comment = comments.findOne(commentId) ?: throw NotFoundException()
        if (comment.describedTicket != null) {
            throw NotFoundException()
        }
        return CommentResult(comment)
    }

    @PreAuthorize(AuthExpr.EDIT_COMMENT)
    override fun updateComment(@P("authCommentId") commentId: UUID, @Valid updateComment: UpdateComment): CommentResult? {

        val comment = comments.findOne(commentId) ?: throw NotFoundException()
        if (comment.describedTicket != null) {
            throw NotFoundException()
        }
        if (updateComment.text != null) {
            comment.text = updateComment.text
        }
        if (updateComment.mentionedTicketIds != null) {
            comment.mentionedTickets.clear()
            for (ticketId: UUID in updateComment.mentionedTicketIds) {
                val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
                comment.mentionedTickets.add(ticket)
            }
        }
        if (updateComment.loggedTime != null) {
            for (loggeTime in comment.loggedTimes) {
                loggedTimeService.deleteLoggedTime(loggeTime.id)
            }
            comment.loggedTimes.clear()
            for (createLoggedTime: CreateLoggedTime in updateComment.loggedTime) {
                createLoggedTime.commentId = comment.id
                val result = loggedTimeService.createLoggedTime(createLoggedTime, comment.id)
                val loggedTime = loggedTimes.findOne(result.id) ?: throw NotFoundException()
                comment.loggedTimes.add(loggedTime)
            }
        }

        return CommentResult(comment)
    }

    @PreAuthorize(AuthExpr.EDIT_COMMENT)
    override fun deleteComment(@P("authCommentId") commentId: UUID) {
        val comment = comments.findOne(commentId) ?: throw NotFoundException()
        if (comment.describedTicket != null) {
            throw NotFoundException()
        }
        comments.delete(comment)
    }

}