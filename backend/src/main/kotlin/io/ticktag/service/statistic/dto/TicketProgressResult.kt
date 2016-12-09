package io.ticktag.service.statistic.dto

import java.time.Duration

data class TicketProgressResult(
        val loggedTime: Duration,
        val currentEstimatedTime: Duration,
        val totalLoggedTime: Duration,
        val totalCurrentEstimatedTime: Duration
)