package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventStateChanged

class TicketEventStateChangedResult(e: TicketEventStateChanged) : TicketEventResult(e) {
    val srcState: Boolean
    val dstState: Boolean

    init {
        srcState = e.srcState
        dstState = e.dstState
    }
}