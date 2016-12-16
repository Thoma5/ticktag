package io.ticktag.service.comment.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.loggedtime.LoggedTimeRepository
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.command.service.CommandService
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.dto.UpdateComment
import io.ticktag.service.comment.service.CommentService
import io.ticktag.service.loggedtime.dto.CreateLoggedTime
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
        private val commands: CommandService
) : CommentService {

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun listCommentsForTicket(@P("authTicketId") ticketId: UUID): List<CommentResult> {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        return ticket.comments.filter { c -> !c.isDescription }.map(::CommentResult)
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

        commands.applyCommands(newComment, createComment.commands, principal)

        return CommentResult(newComment)
    }

    @PreAuthorize(AuthExpr.READ_COMMENT)
    override fun getComment(@P("authCommentId") commentId: UUID): CommentResult? {
        val comment = comments.findOne(commentId) ?: throw NotFoundException()
        if (comment.isDescription) {
            throw NotFoundException()
        }
        return CommentResult(comment)
    }

    @PreAuthorize(AuthExpr.EDIT_COMMENT)
    override fun updateComment(@P("authCommentId") commentId: UUID, @Valid updateComment: UpdateComment): CommentResult? {

        val comment = comments.findOne(commentId) ?: throw NotFoundException()
        if (comment.isDescription) {
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
        if (comment.isDescription) {
            throw NotFoundException()
        }
        comments.delete(comment)
    }
}
