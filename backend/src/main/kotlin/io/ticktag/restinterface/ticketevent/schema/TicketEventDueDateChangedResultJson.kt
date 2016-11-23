package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventDueDateChangedResult
import java.time.Instant

class TicketEventDueDateChangedResultJson(e: TicketEventDueDateChangedResult) : TicketEventResultJson(e) {
    val srcDueDate: Instant?
    val dstDueDate: Instant?

    init {
        srcDueDate = e.srcDueDate
        dstDueDate = e.dstDueDate
    }
}