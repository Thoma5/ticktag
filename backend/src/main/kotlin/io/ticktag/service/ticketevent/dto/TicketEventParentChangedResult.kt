package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketEventParentChanged

class TicketEventParentChangedResult(e: TicketEventParentChanged) : TicketEventResult(e) {
    val srcParent: Ticket?
    val dstParent: Ticket?

    init {
        srcParent = e.srcParent
        dstParent = e.dstParent
    }
}