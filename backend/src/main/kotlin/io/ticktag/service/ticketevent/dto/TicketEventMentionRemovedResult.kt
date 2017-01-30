package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventMentionRemoved
import java.util.*

class TicketEventMentionRemovedResult(e: TicketEventMentionRemoved) : TicketEventResult(e) {
    val commentId: UUID
    val mentionedTicketId: UUID

    init {
        commentId = e.comment.id
        mentionedTicketId = e.mentionedTicket.id
    }
}