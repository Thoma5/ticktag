package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventTitleChanged

class TicketEventTitleChangedResult(e: TicketEventTitleChanged) : TicketEventResult(e) {
    val srcTitle: String
    val dstTitle: String

    init {
        srcTitle = e.srcTitle
        dstTitle = e.dstTitle
    }
}