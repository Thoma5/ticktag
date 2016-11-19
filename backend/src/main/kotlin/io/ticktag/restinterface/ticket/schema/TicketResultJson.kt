package io.ticktag.restinterface.ticket.schema

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.ticket.dto.TicketResult
import java.time.Duration
import java.time.Instant
import java.util.*

class TicketResultJson(
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

        val subTicketIds: List<UUID>,
        val parentTicketId: UUID?,
        val createdBy: UUID,
        val tagIds: List<UUID>?,
        val mentoningCommentIds: List<UUID>,
        val commentIds: List<UUID>

) {
    constructor(t: TicketResult) : this(id = t.id, number = t.number, createTime = t.createTime, title = t.title,
            open = t.open, storyPoints = t.storyPoints, initialEstimatedTime = t.initialEstimatedTime, currentEstimatedTime = t.currentEstimatedTime,
            dueDate = t.dueDate, description = t.description, projectId = t.projectId, subTicketIds = t.subTicketIds, parentTicketId = t.parentTicketId,
            createdBy = t.createdBy, tagIds = t.tagIds, mentoningCommentIds = t.mentoningCommentIds, commentIds = t.commentIds)
}