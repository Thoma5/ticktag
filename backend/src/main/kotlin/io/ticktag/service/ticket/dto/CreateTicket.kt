package io.ticktag.service.ticket.dto

import io.ticktag.restinterface.ticket.schema.CreateTicketRequestJson
import java.time.Duration
import java.time.Instant
import java.util.*

import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class CreateTicket(
        @field:Size(min = 1, max = 100) val title: String,
        val open: Boolean,
        @field:DecimalMin("0") val storyPoints: Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        @field:Size(min = 1, max = 5000) val description: String,
        val projectID: UUID,
        val ticketAssignments: List<TicketAssignment>?,
        val subTickets: List<CreateTicket>?,
        val existingSubTicketIds: List<UUID>?,
        var parentTicket: UUID?
) {
    constructor(req: CreateTicketRequestJson) : this(
            req.title, req.open, req.storyPoints, req.initialEstimatedTime,
            req.currentEstimatedTime, req.dueDate, req.description, req.projectId, req.ticketAssignments?.map(::TicketAssignment), req.subTickets?.map(::CreateTicket), req.existingSubTicketIds, req.partenTicketId)


}