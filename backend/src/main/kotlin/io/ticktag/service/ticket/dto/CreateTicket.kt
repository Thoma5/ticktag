package io.ticktag.service.ticket.dto

import io.ticktag.restinterface.ticket.schema.CreateTicketRequestJson
import io.ticktag.service.command.dto.Command
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.AssertTrue

import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class CreateTicket(
        @field:Size(min = 1, max = 100) val title: String,
        val open: Boolean,
        @field:DecimalMin("0") val storyPoints: Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        @field:Size(min = 0, max = 5000) val description: String,
        val projectID: UUID,
        val ticketAssignments: List<TicketAssignment>,
        val subTickets: List<CreateTicket>,
        val existingSubTicketIds: List<UUID>,
        val parentTicket: UUID?,
        val commands: List<Command>
) {
    constructor(req: CreateTicketRequestJson, subtickets: List<CreateTicket>, commands: List<Command>) : this(
            req.title, req.open, req.storyPoints, req.initialEstimatedTime,
            req.currentEstimatedTime, req.dueDate, req.description, req.projectId,
            req.ticketAssignments.map(::TicketAssignment), subtickets,
            req.existingSubTicketIds, req.parentTicketId, commands)


}