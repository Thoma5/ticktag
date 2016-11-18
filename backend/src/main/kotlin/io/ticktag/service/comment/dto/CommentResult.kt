package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Instant
import java.util.*


data class CommentResult(
        val id: UUID,
        val createTime: Instant,
        val text: String,
        val userId: UUID,
        val ticketId: UUID
) {
    constructor(c: Comment) : this(id = c.id, createTime = c.createTime, text = c.text, userId = c.user.id, ticketId = c.ticket.id)
}