package io.ticktag.service.ticket.dto

import io.ticktag.util.PositiveDuration
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class UpdateTicket(
        @field:Size(min = 1, max = 100) val title: String?,

        val open: Boolean?,

        @field:DecimalMin("0") val storyPoints: Int?,
        val storyPointsNull: Boolean,

        @field:PositiveDuration val initialEstimatedTime: Duration?,
        val initialEstimatedTimeNull: Boolean,

        @field:PositiveDuration val currentEstimatedTime: Duration?,
        val currentEstimatedTimeNull: Boolean,

        val dueDate: Instant?,
        val dueDateNull: Boolean,

        @field:Size(min = 0, max = 5000) val description: String?,

        val parentTicket: UUID?,
        val parentTicketNull: Boolean
)
