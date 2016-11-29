package io.ticktag.service.statistic.dto

import java.time.Duration

data class TicketProgressResult(
        val totalLoggedTime: Duration,
        val currentEstimatedTime: Duration
)