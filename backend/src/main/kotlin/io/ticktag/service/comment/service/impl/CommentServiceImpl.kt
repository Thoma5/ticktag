package io.ticktag.service.comment.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.service.CommentService
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Instant
import javax.inject.Inject

@TicktagService
open class CommentServiceImpl @Inject constructor(
        private val comments: CommentRepository,
        private val tickets: TicketRepository,
        private val users: UserRepository

) :CommentService{
    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun listComments(): List<CommentResult> {
        return comments.findAll().map(::CommentResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun createComment(createComment: CreateComment): CommentResult {
        val text = createComment.text
        val ticket = tickets.findOne(createComment.ticketID)?: throw NotFoundException()
        val user = users.findOne(createComment.userID)?: throw NotFoundException()
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime,text,user,ticket)
        comments.insert(newComment)
        return CommentResult(newComment)
    }


}