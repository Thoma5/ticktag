package io.ticktag.service.ticket.dto

import io.ticktag.restinterface.ticket.schema.UpdateTicketRequestJson
import io.ticktag.util.CheckDuration
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class UpdateTicket(
        @field:Size(min = 1, max = 100) val title: String?,
        val open: Boolean?,
        @field:DecimalMin("0") val storyPoints: Int?,
        @field:CheckDuration val initialEstimatedTime: Duration?,
        @field:CheckDuration val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        @field:Size(min = 0, max = 5000) val description: String?,
        val parentTicket: UUID?

) {
    constructor(req: UpdateTicketRequestJson) : this(
            req.title, req.open, req.storyPoints, req.initialEstimatedTime, req.currentEstimatedTime, req.dueDate,
            req.description, req.parentTicketId)
}