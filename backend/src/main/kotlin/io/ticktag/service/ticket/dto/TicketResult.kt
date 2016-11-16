package io.ticktag.service.ticket.dto

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Duration
import java.time.Instant
import java.util.*


data class TicketResult(
        val id: UUID,
        val number:Int,
        val createTime:Instant,
        val title: String,
        val open:Boolean,
        val storyPoints:Int?,
        val initialEstimatedTime:Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String
) {
    constructor(t: Ticket) : this(id = t.id,number = t.number,createTime = t.createTime,title = t.title,
                open = t.open,storyPoints = t.storyPoints, initialEstimatedTime = t.initialEstimatedTime,currentEstimatedTime = t.currentEstimatedTime,
            dueDate = t.dueDate,description = t.descriptionComment.text)
}
