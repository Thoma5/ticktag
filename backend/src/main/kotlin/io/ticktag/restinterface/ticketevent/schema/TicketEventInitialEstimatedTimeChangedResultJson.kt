package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventInitialEstimatedTimeChangedResult
import java.time.Duration

class TicketEventInitialEstimatedTimeChangedResultJson(e: TicketEventInitialEstimatedTimeChangedResult) : TicketEventResultJson(e) {
    val srcInitialEstimatedTime: Duration?
    val dstInitialEstimatedTime: Duration?

    init {
        srcInitialEstimatedTime = e.srcInitialEstimatedTime
        dstInitialEstimatedTime = e.dstInitialEstimatedTime
    }
}