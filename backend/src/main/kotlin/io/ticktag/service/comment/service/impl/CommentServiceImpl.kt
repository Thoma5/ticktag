package io.ticktag.service.comment.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.ticket.entity.Comment
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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import java.time.Instant
import java.util.*
import javax.inject.Inject

@TicktagService
open class CommentServiceImpl @Inject constructor(
        private val comments: CommentRepository,
        private val tickets: TicketRepository,
        private val users: UserRepository

) :CommentService{
    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun listComments(): List<CommentResult> {
        return comments.findAll().filter{ c -> c.describedTicket==null}.map(::CommentResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun createComment(createComment: CreateComment, principal: Principal): CommentResult {

        val text = createComment.text
        val ticket = tickets.findOne(createComment.ticketID)?: throw NotFoundException()
        val user = users.findOne(principal.id)?: throw NotFoundException()
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime,text,user,ticket)
        comments.insert(newComment)
        return CommentResult(newComment)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun getComment(commentID: UUID): CommentResult {
        val comment = comments.findOne(commentID)?: throw NotFoundException()
        if (comment.describedTicket != null ){
            throw NotFoundException()
        }
        return CommentResult(comment)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun updateComment(commentID: UUID, updateComment: UpdateComment): CommentResult {

        val comment = comments.findOne(commentID)?: throw NotFoundException()
        if (comment.describedTicket != null ){
            throw NotFoundException()
        }
        comment.text = updateComment.text

        return CommentResult(comment)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun deleteComment(commentID: UUID) {
        val comment = comments.findOne(commentID)?: throw NotFoundException()
        if (comment.describedTicket != null){
            throw NotFoundException()
        }
        comments.delete(comment)
    }

}