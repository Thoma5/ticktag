package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.user.entity.Role
import java.time.Duration
import java.time.Instant
import java.util.*

data class CreateTicketRequestJson(
        val number:Int,
        val title: String,
        val open:Boolean,
        val storyPoints:Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String,
        val projectId: UUID,
        val subTickets: List<CreateTicketRequestJson>,
        val existingSubTicketIds: List<UUID>,
        val partenTicket: UUID?
)
