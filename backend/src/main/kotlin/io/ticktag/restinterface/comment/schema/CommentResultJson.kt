package io.ticktag.restinterface.comment.schema

import io.ticktag.service.comment.dto.CommentResult
import java.time.Instant
import java.util.*


class CommentResultJson(val id: UUID,
                        val createTime: Instant,
                        val text: String,
                        val userID: UUID,
                        val ticketID: UUID) {
    constructor(c: CommentResult) : this(id = c.id, createTime = c.createTime, text = c.text, userID = c.userID, ticketID = c.ticketID)
}