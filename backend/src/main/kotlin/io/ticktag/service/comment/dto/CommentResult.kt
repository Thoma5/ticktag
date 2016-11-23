package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Comment
import java.time.Instant
import java.util.*


data class CommentResult(
        val id: UUID,
        val createTime: Instant,
        val text: String,
        val userId: UUID,
        val ticketId: UUID,
        val mentionedTicketIds: List<UUID>,
        val loggedTimeIds: List<UUID>
) {
    constructor(c: Comment) : this(id = c.id, createTime = c.createTime, text = c.text, userId = c.user.id, ticketId = c.ticket.id,
            mentionedTicketIds = c.mentionedTickets.map { t -> t.id }, loggedTimeIds = c.loggedTimes.map { time -> time.id })
}