package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventTagRemoved
import io.ticktag.persistence.ticket.entity.TicketTag

class TicketEventTagRemovedResult(e: TicketEventTagRemoved) : TicketEventResult(e) {
    val tag: TicketTag

    init {
        tag = e.tag
    }
}