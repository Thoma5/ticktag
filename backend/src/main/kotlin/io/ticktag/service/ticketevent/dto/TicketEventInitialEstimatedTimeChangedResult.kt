package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventInitialEstimatedTimeChanged
import java.time.Duration

class TicketEventInitialEstimatedTimeChangedResult(e: TicketEventInitialEstimatedTimeChanged) : TicketEventResult(e) {
    val srcInitialEstimatedTime: Duration?
    val dstInitialEstimatedTime: Duration?

    init {
        srcInitialEstimatedTime = e.srcInitialEstimatedTime
        dstInitialEstimatedTime = e.dstInitialEstimatedTime
    }
}