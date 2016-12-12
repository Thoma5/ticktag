package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventParentChangedResult
import java.util.*

class TicketEventParentChangedResultJson(e: TicketEventParentChangedResult) : TicketEventResultJson(e) {
    val srcParentId: UUID?
    val dstParentId: UUID?

    init {
        srcParentId = e.srcParentId
        dstParentId = e.dstParentId
    }
}