package io.ticktag.service.ticket.dto

import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import java.time.Duration
import java.time.Instant
import java.util.*

data class TicketProgressResult(
        val duration: Duration,
        val currentEstimatedTime: Duration
)