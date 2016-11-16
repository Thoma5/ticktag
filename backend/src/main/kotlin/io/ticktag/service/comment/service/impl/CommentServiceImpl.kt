package io.ticktag.service.comment.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.service.AuthExpr
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.service.CommentService
import org.springframework.security.access.prepost.PreAuthorize
import javax.inject.Inject

@TicktagService
open class CommentServiceImpl @Inject constructor(
        private val comments: CommentRepository
) :CommentService{
    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun listComments(): List<CommentResult> {
        return comments.findAll().map(::CommentResult)
    }
}