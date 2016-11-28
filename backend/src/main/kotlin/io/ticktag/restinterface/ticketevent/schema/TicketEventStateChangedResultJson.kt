package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventStateChangedResult

class TicketEventStateChangedResultJson(e: TicketEventStateChangedResult) : TicketEventResultJson(e) {
    val srcState: Boolean
    val dstState: Boolean

    init {
        srcState = e.srcState
        dstState = e.dstState
    }
}