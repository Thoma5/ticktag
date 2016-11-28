package io.ticktag.restinterface.ticket.schema

import io.ticktag.service.ticket.dto.TicketProgressResult
import java.time.Duration

data class TicketProgressResultJson(
        val duration: Duration,
        val currentEstimatedTime: Duration
) {
    constructor(t: TicketProgressResult) : this(duration = t.duration, currentEstimatedTime = t.currentEstimatedTime)

}