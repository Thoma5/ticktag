package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventTitleChangedResult

class TicketEventTitleChangedResultJson(e: TicketEventTitleChangedResult) : TicketEventResultJson(e) {
    val srcTitle: String
    val dstTitle: String

    init {
        srcTitle = e.srcTitle
        dstTitle = e.dstTitle
    }
}