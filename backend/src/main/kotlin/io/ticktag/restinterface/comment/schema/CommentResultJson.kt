package io.ticktag.restinterface.comment.schema

import io.ticktag.service.comment.dto.CommentResult
import java.time.Instant
import java.util.*


data class CommentResultJson(val id: UUID,
                             val createTime: Instant,
                             val text: String,
                             val userId: UUID,
                             val ticketId: UUID) {
    constructor(c: CommentResult) : this(id = c.id, createTime = c.createTime, text = c.text, userId = c.userId, ticketId = c.ticketId)
}