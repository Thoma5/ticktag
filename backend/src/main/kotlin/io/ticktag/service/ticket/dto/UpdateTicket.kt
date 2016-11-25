package io.ticktag.service.ticket.dto

import io.ticktag.restinterface.ticket.schema.UpdateTicketRequestJson
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class UpdateTicket(
        @field:Size(min = 1, max = 100) val title: String?,
        val open: Boolean?,
        @field:DecimalMin("0") val storyPoints: Int?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        @field:Size(min = 1, max = 5000) val description: String?,
        val ticketAssignments: List<TicketAssignment>?,
        val subTickets: List<CreateTicket>?,
        val existingSubTicketIds: List<UUID>?,
        val parentTicket: UUID?

) {
    constructor(req: UpdateTicketRequestJson) : this(
            req.title, req.open, req.storyPoints, req.currentEstimatedTime, req.dueDate,
            req.description, req.ticketAssignments?.map(::TicketAssignment), req.subTickets?.map(::CreateTicket), req.existingSubTicketIds, req.parentTicketId)
}