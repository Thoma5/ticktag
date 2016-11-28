package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventCurrentEstimatedTimeChangedResult
import java.time.Duration

class TicketEventCurrentEstimatedTimeChangedResultJson(e: TicketEventCurrentEstimatedTimeChangedResult) : TicketEventResultJson(e) {
    val srcCurrentEstimatedTime: Duration?
    val dstCurrentEstimatedTime: Duration?

    init {
        srcCurrentEstimatedTime = e.srcCurrentEstimatedTime
        dstCurrentEstimatedTime = e.dstCurrentEstimatedTime
    }
}