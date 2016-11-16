package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Instant
import java.util.*


class CommentResult(
        val id: UUID,
        val createTime: Instant,
        val text: String,
        val userID: UUID,
        val ticketID: UUID
) {
    constructor(c: Comment) : this(id = c.id, createTime = c.createTime, text = c.text, userID = c.user.id, ticketID = c.ticket.id)
}