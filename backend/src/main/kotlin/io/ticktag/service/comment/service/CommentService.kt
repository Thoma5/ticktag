package io.ticktag.service.comment.service

import io.ticktag.service.comment.dto.CommentResult

/**
 * Created by stefandraskovits on 17/11/2016.
 */
interface CommentService {
    fun listComments():List<CommentResult>
}