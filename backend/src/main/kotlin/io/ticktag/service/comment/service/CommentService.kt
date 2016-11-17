package io.ticktag.service.comment.service

import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment

/**
 * Created by stefandraskovits on 17/11/2016.
 */
interface CommentService {
    fun listComments():List<CommentResult>
    fun createComment(createComment: CreateComment):CommentResult
}