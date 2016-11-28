package io.ticktag.service.comment.service.impl

import io.ticktag.TicktagService
import io.ticktag.library.unicode.NameNormalizationLibrary
import io.ticktag.persistence.loggedtime.LoggedTimeRepository
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.TicketRepository
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
import io.ticktag.service.ticket.dto.UpdateTicket
import io.ticktag.service.ticket.service.TicketService
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import io.ticktag.service.tickettagrelation.services.TicketTagRelationService
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
        private val ticketService: TicketService,
        private val nn: NameNormalizationLibrary,
        private val ticketTagRelationService: TicketTagRelationService,
        private val ticketAssignmentService: TicketAssignmentService
) : CommentService {

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun listCommentsForTicket(@P("authTicketId") ticketId: UUID): List<CommentResult> {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        return ticket.comments.filter { c -> c.describedTicket == null }.map(::CommentResult)
    }

    // TODO change events?
    @PreAuthorize(AuthExpr.CREATE_COMMENT)
    override fun createComment(@Valid createComment: CreateComment, principal: Principal, @P("authTicketId") ticketId: UUID): CommentResult {
        val text = createComment.text
        val ticket = tickets.findOne(createComment.ticketId) ?: throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime, text, user, ticket)
        comments.insert(newComment)

        val errors = mutableListOf<ValidationError>()

        for ((index, command) in createComment.commands.withIndex()) {
            when (command) {
                is CommentCommand.Assign -> {
                    val assignUser = users.findByUsername(command.user)
                    val assignTag = assignmentTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                    if (assignUser != null && assignTag != null) {
                        tryCommand(errors, index) {
                            ticketAssignmentService.createOrGetIfExistsTicketAssignment(ticket.id, assignTag.id, assignUser.id)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is CommentCommand.Unassign -> {
                    val removeUser = users.findByUsername(command.user)
                    if (removeUser != null) {
                        if (command.tag == null) {
                            tryCommand(errors, index) {
                                ticketAssignmentService.deleteTicketAssignments(ticket.id, removeUser.id)
                            }
                        } else {
                            val removeTag = assignmentTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                            if (removeTag != null) {
                                tryCommand(errors, index) {
                                    ticketAssignmentService.deleteTicketAssignment(ticket.id, removeTag.id, removeUser.id)
                                }
                            } else {
                                failedCommand(errors, index)
                            }
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is CommentCommand.Close -> {
                    tryCommand(errors, index) {
                        ticketService.updateTicket(UpdateTicket(null, false, null, null, null, null, null, null), ticket.id, principal)
                    }
                }
                is CommentCommand.Reopen -> {
                    tryCommand(errors, index) {
                        ticketService.updateTicket(UpdateTicket(null, true, null, null, null, null, null, null), ticket.id, principal)
                    }
                }
                is CommentCommand.Tag -> {
                    val tag = ticketTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                    if (tag != null) {
                        tryCommand(errors, index) {
                            ticketTagRelationService.createOrGetIfExistsTicketTagRelation(ticket.id, tag.id)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is CommentCommand.Untag -> {
                    val tag = ticketTags.findByNormalizedNameAndProjectId(nn.normalize(command.tag), ticket.project.id)
                    if (tag != null) {
                        tryCommand(errors, index) {
                            ticketTagRelationService.deleteTicketTagRelation(ticket.id, tag.id)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
                is CommentCommand.Est -> {
                    tryCommand(errors, index) {
                        ticketService.updateTicket(UpdateTicket(null, null, null, null, command.duration, null, null, null), ticket.id, principal)
                    }
                }
                is CommentCommand.Time -> {
                    val cat = timeCategories.findByNormalizedNameAndProjectId(nn.normalize(command.category), ticket.project.id)
                    if (cat != null) {
                        tryCommand(errors, index) {
                            // TODO why needs createLoggedTime the comment id twice???
                            loggedTimeService.createLoggedTime(CreateLoggedTime(command.duration, newComment.id, cat.id), newComment.id)
                        }
                    } else {
                        failedCommand(errors, index)
                    }
                }
            }
        }

        for (reference in createComment.mentionedTicketNumbers) {
            val referencedTicket = tickets.findByNumber(reference)
            if (referencedTicket != null) {
                newComment.mentionedTickets.add(referencedTicket)
            } else {
                errors.add(ValidationError("createComment.mentionedTicketNumbers", ValidationErrorDetail.Other("$reference")))
            }
        }

        if (errors.isNotEmpty()) {
            throw TicktagValidationException(errors)
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

    private fun failedCommand(errors: MutableList<ValidationError>, index: Int) {
        errors.add(ValidationError("createComment.commands", ValidationErrorDetail.Other("$index")))
    }

    private fun tryCommand(errors: MutableList<ValidationError>, index: Int, fn: () -> Unit) {
        try {
            fn()
        } catch (ex: TicktagValidationException) {
            failedCommand(errors, index)
        } catch (ex: NotFoundException) {
            failedCommand(errors, index)
        }
    }
}
