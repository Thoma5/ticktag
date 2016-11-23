package io.ticktag.service.ticket.dto

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketTag
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
        var ticketAssignments: List<TicketAssignmentResult>?,
        var subTicketIds: List<UUID>,
        val parentTicketId: UUID?,
        val createdBy: UUID,
        val tagIds: List<UUID>?,
        val mentoningCommentIds: List<UUID>,
        val commentIds: List<UUID>


) {
    constructor(t: Ticket) : this(id = t.id, number = t.number, createTime = t.createTime, title = t.title,
            open = t.open, storyPoints = t.storyPoints, initialEstimatedTime = t.initialEstimatedTime, currentEstimatedTime = t.currentEstimatedTime,
            dueDate = t.dueDate, description = t.descriptionComment.text, projectId = t.project.id, ticketAssignments = t.assignedTicketUsers.map(::TicketAssignmentResult), subTicketIds = t.subTickets.map(Ticket::id),
            parentTicketId = t.parentTicket?.id, createdBy = t.createdBy.id, tagIds = t.tags.map(TicketTag::id), commentIds = t.comments.filter { c -> c.describedTicket == null }.map(Comment::id),
            mentoningCommentIds = t.mentioningComments.map(Comment::id))
}
