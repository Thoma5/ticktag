package io.ticktag.service.ticket.dto

import java.time.Duration

data class TicketProgressResult(
        val duration: Duration,
        val currentEstimatedTime: Duration
)