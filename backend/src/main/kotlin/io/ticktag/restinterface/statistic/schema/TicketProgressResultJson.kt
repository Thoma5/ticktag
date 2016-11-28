package io.ticktag.restinterface.statistic.schema

import io.ticktag.service.statistic.dto.TicketProgressResult
import java.time.Duration

data class TicketProgressResultJson(
        val totalLoggedTime: Duration,
        val currentEstimatedTime: Duration
) {
    constructor(t: TicketProgressResult) : this(totalLoggedTime = t.totalLoggedTime, currentEstimatedTime = t.currentEstimatedTime)

}