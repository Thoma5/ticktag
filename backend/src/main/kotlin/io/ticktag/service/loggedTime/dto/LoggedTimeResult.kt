package io.ticktag.service.loggedTime.dto

import io.ticktag.persistence.ticket.entity.LoggedTime
import java.time.Duration
import java.util.*

data class LoggedTimeResult(
        val id: UUID,
        val time: Duration,
        val commentId: UUID,
        val categoryId: UUID
) {
    constructor(l: LoggedTime) : this(id = l.id, time = l.time, commentId = l.comment.id, categoryId = l.category.id)
}