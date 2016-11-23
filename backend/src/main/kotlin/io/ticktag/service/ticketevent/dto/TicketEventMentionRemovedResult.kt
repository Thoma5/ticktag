package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventMentionRemoved
import java.util.*

class TicketEventMentionRemovedResult(e: TicketEventMentionRemoved) : TicketEventResult(e) {
    val commentId: UUID
    val mentionedTicketId: UUID
    val mentionedTicketNumber: Int
    val mentionedTicketTitle: String

    init {
        commentId = e.comment.id
        mentionedTicketId = e.mentionedTicket.id
        mentionedTicketNumber = e.mentionedTicket.number
        mentionedTicketTitle = e.mentionedTicket.title
    }
}