package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketEventMentionRemoved

class TicketEventMentionRemovedResult(e: TicketEventMentionRemoved) : TicketEventResult(e) {
    val comment: Comment
    val mentionedTicket: Ticket

    init {
        comment = e.comment
        mentionedTicket = e.mentionedTicket
    }
}