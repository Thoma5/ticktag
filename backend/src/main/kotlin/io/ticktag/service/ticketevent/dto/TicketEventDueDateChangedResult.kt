package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventDueDateChanged
import java.time.Instant

class TicketEventDueDateChangedResult(e: TicketEventDueDateChanged) : TicketEventResult(e) {
    val srcDueDate: Instant?
    val dstDueDate: Instant?

    init {
        srcDueDate = e.srcDueDate
        dstDueDate = e.dstDueDate
    }
}