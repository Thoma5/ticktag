package io.ticktag.restinterface.statistic.schema

import io.ticktag.service.statistic.dto.TicketProgressResult
import java.time.Duration

data class TicketProgressResultJson(
        val currentEstimatedTime: Duration,
        val loggedTime: Duration,
        val totalLoggedTime: Duration,
        val totalCurrentEstimatedTime: Duration
) {
    constructor(t: TicketProgressResult) : this(currentEstimatedTime = t.currentEstimatedTime,
            loggedTime = t.loggedTime, totalLoggedTime = t.totalLoggedTime,
            totalCurrentEstimatedTime = t.totalCurrentEstimatedTime)

}