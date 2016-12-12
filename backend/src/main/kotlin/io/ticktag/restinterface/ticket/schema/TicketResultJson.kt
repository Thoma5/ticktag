package io.ticktag.restinterface.ticket.schema

import io.ticktag.restinterface.ticketuserrelation.schema.TicketUserRelationResultJson
import io.ticktag.service.ticket.dto.TicketResult
import java.time.Duration
import java.time.Instant
import java.util.*

data class TicketResultJson(
        val id: UUID,
        val number: Int,
        val createTime: Instant,
        val title: String,
        val open: Boolean,
        val storyPoints: Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val totalInitialEstimatedTime: Duration?,
        val totalCurrentEstimatedTime: Duration?,
        val initialProgress: Float?,
        val progress: Float?,
        val loggedTime: Duration?,
        val dueDate: Instant?,
        val description: String,
        val projectId: UUID,
        val ticketUserRelations: List<TicketUserRelationResultJson>,
        val subTicketIds: List<UUID>,
        val parentTicketId: UUID?,
        val createdBy: UUID,
        val tagIds: List<UUID>?,
        val referencingTicketIds: List<UUID>,
        val referencedTicketIds: List<UUID>,
        val commentIds: List<UUID>

) {
    constructor(t: TicketResult) : this(id = t.id, number = t.number, createTime = t.createTime, title = t.title,
            open = t.open, storyPoints = t.storyPoints, initialEstimatedTime = t.initialEstimatedTime, currentEstimatedTime = t.currentEstimatedTime,
            totalInitialEstimatedTime = t.progress.initalEstimatedTime,totalCurrentEstimatedTime = t.progress.currentEstimatedTime,
            initialProgress = t.progress.initialProgress, progress = t.progress.progress, loggedTime = t.progress.loggedTime, dueDate = t.dueDate,
            description = t.description, projectId = t.projectId, ticketUserRelations = t.ticketAssignments.map(::TicketUserRelationResultJson),
            subTicketIds = t.subTicketIds, parentTicketId = t.parentTicketId, createdBy = t.createdBy,
            tagIds = t.tagIds, referencingTicketIds = t.referencingTicketIds,
            referencedTicketIds = t.referencedTicketIds, commentIds = t.commentIds)
}