package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventMentionAdded
import java.util.*

class TicketEventMentionAddedResult(e: TicketEventMentionAdded) : TicketEventResult(e) {
    val commentId: UUID
    val mentionedTicketId: UUID

    init {
        commentId = e.comment.id
        mentionedTicketId = e.mentionedTicket.id
    }
}