package io.ticktag.service.comment.service

import io.ticktag.service.Principal
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.dto.UpdateComment
import java.util.*


interface CommentService {
    fun listComments(): List<CommentResult>
    fun getComment(commentID: UUID): CommentResult?
    fun createComment(createComment: CreateComment, principal: Principal): CommentResult
    fun updateComment(commentID: UUID, updateComment: UpdateComment): CommentResult?

    fun deleteComment(commentID: UUID)
}