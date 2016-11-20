package io.ticktag.service.comment.service

import io.ticktag.service.Principal
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.dto.UpdateComment
import java.util.*


interface CommentService {
    fun listComments(pId: UUID): List<CommentResult>
    fun listCommentsForTicket(tId: UUID): List<CommentResult>
    fun getComment(commentId: UUID): CommentResult?
    fun createComment(createComment: CreateComment, principal: Principal, ticketId: UUID): CommentResult
    fun updateComment(commentId: UUID, updateComment: UpdateComment): CommentResult?
    fun deleteComment(commentId: UUID)

}