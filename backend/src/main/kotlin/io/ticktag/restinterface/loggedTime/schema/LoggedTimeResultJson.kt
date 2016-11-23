package io.ticktag.restinterface.loggedTime.schema

import io.ticktag.service.loggedTime.dto.LoggedTimeResult
import java.time.Duration
import java.util.*

data class LoggedTimeResultJson(
        val id: UUID,
        val time: Duration,
        val commentId: UUID,
        val categoryId: UUID
) {
    constructor(l: LoggedTimeResult) : this(id = l.id, time = l.time, commentId = l.commentId, categoryId = l.categoryId)
}