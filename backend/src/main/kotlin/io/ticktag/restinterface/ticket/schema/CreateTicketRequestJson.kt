package io.ticktag.restinterface.ticket.schema

import io.ticktag.restinterface.ticketuserrelation.schema.CreateTicketUserRelationRequestJson
import java.time.Duration
import java.time.Instant
import java.util.*

data class CreateTicketRequestJson(
        val title: String,
        val open: Boolean,
        val storyPoints: Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String,
        val projectId: UUID,
        val ticketAssignments: List<CreateTicketUserRelationRequestJson>,
        val subTickets: List<CreateTicketRequestJson>,
        val existingSubTicketIds: List<UUID>,
        val parentTicketId: UUID?
)
