package io.ticktag.restinterface.ticket.schema

import io.ticktag.restinterface.ticketuserrelation.schema.TicketUserRelationResultJson
import io.ticktag.service.ticket.dto.TicketOverviewResult
import java.time.Duration
import java.time.Instant
import java.util.*

data class TicketOverviewResultJson(
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
        val ticketUserRelations: List<TicketUserRelationResultJson>,
        val parentTicket: TicketOverviewResultJson?,
        val tagIds: List<UUID>?

) {
    constructor(t: TicketOverviewResult) : this(id = t.id, number = t.number, createTime = t.createTime, title = t.title,
            open = t.open, storyPoints = t.storyPoints, initialEstimatedTime = t.initialEstimatedTime, currentEstimatedTime = t.currentEstimatedTime,
            totalInitialEstimatedTime = t.progress.initalEstimatedTime,totalCurrentEstimatedTime = t.progress.currentEstimatedTime,
            initialProgress = t.progress.initialProgress, progress = t.progress.progress, loggedTime = t.progress.loggedTime, dueDate = t.dueDate,
            description = t.description, ticketUserRelations = t.ticketAssignments.map(::TicketUserRelationResultJson),
            parentTicket = if(t.parentTicket == null) null else TicketOverviewResultJson(t.parentTicket),
            tagIds = t.tagIds)
}