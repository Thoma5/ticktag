package io.ticktag.service.ticket.dto

import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import java.time.Duration
import java.time.Instant
import java.util.*


data class TicketResult(
        val id: UUID,
        val number: Int,
        val createTime: Instant,
        val title: String,
        val open: Boolean,
        val storyPoints: Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String,
        val projectId: UUID,
        val ticketAssignments: List<TicketAssignmentResult>,
        val subTicketIds: List<UUID>,
        val parentTicketId: UUID?,
        val createdBy: UUID,
        val tagIds: List<UUID>?,
        val referencingTicketIds: List<UUID>,
        val referencedTicketIds: List<UUID>,
        val commentIds: List<UUID>
)
