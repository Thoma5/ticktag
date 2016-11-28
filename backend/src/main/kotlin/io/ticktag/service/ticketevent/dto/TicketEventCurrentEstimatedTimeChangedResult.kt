package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventCurrentEstimatedTimeChanged
import java.time.Duration

class TicketEventCurrentEstimatedTimeChangedResult(e: TicketEventCurrentEstimatedTimeChanged) : TicketEventResult(e) {
    val srcCurrentEstimatedTime: Duration?
    val dstCurrentEstimatedTime: Duration?

    init {
        srcCurrentEstimatedTime = e.srcCurrentEstimatedTime
        dstCurrentEstimatedTime = e.dstCurrentEstimatedTime
    }
}