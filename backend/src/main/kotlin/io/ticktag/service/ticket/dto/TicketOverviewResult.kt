package io.ticktag.service.ticket.dto

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import java.time.Duration
import java.time.Instant
import java.util.*


data class TicketOverviewResult(
        val id: UUID,
        val number: Int,
        val createTime: Instant,
        val title: String,
        val open: Boolean,
        val storyPoints: Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val progress: ProgressResult,
        val dueDate: Instant?,
        val description: String,
        val ticketAssignments: List<TicketAssignmentResult>,
        val parentTicket: TicketOverviewResult?,
        val tagIds: List<UUID>?
)
{
    constructor(t: Ticket) : this(id = t.id, number = t.number, createTime = t.createTime, title = t.title,
            open = t.open, storyPoints = t.storyPoints, initialEstimatedTime = t.initialEstimatedTime, currentEstimatedTime = t.currentEstimatedTime,
            progress = ProgressResult(t.progress), dueDate = t.dueDate, description = t.descriptionComment.text, ticketAssignments = t.assignedTicketUsers.map(::TicketAssignmentResult),
            parentTicket = (if (t.parentTicket == null) null else TicketOverviewResult(t.parentTicket as Ticket)), tagIds = t.tags.map { e -> e.id })

}
