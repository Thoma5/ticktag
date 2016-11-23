package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventTagAdded
import io.ticktag.persistence.ticket.entity.TicketTag

class TicketEventTagAddedResult(e: TicketEventTagAdded) : TicketEventResult(e) {
    val tag: TicketTag

    init {
        tag = e.tag
    }
}