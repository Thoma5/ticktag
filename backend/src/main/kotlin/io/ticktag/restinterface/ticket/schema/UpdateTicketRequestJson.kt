package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.user.entity.Role
import java.time.Duration
import java.time.Instant
import java.util.*

data class UpdateTicketRequestJson(
        val title: String?,
        val open: Boolean?,
        val storyPoints: Int?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String?,
        val subTickets: List<CreateTicketRequestJson>?,
        val existingSubTicketIds: List<UUID>?,
        val partenTicket: UUID?
)
