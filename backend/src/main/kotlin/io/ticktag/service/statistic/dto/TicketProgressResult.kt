package io.ticktag.service.statistic.dto

import java.time.Duration

data class TicketProgressResult(
        val duration: Duration,
        val currentEstimatedTime: Duration
)