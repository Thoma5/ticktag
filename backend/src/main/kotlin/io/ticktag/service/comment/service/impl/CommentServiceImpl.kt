package io.ticktag.service.comment.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.ticket.TicketEventRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.TicketEvent
import io.ticktag.persistence.ticket.entity.TicketEventCommentTextChanged
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.ValidationError
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment

import io.ticktag.service.comment.dto.UpdateComment
import io.ticktag.service.comment.service.CommentService
import org.springframework.context.annotation.Description
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class CommentServiceImpl @Inject constructor(
        private val comments: CommentRepository,
        private val tickets: TicketRepository,
        private val users: UserRepository,
        private val ticketEvents: TicketEventRepository

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
    override fun updateComment(@P("authCommentId") commentId: UUID, @Valid updateComment: UpdateComment, principal: Principal): CommentResult? {
        val user = users.findOne(principal.id) ?: throw NotFoundException()
        val comment = comments.findOne(commentId) ?: throw NotFoundException()
        if (comment.describedTicket != null) {
            throw NotFoundException()
        }

        if (comment.text != updateComment.text)
            ticketEvents.insert(TicketEventCommentTextChanged.create(comment.ticket, user, comment, comment.text, updateComment.text))
        comment.text = updateComment.text

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