package io.ticktag.restinterface.loggedtime.schema

import io.ticktag.service.loggedtime.dto.LoggedTimeResult
import java.time.Duration
import java.util.*

data class LoggedTimeResultJson(
        val id: UUID,
        val time: Duration,
        val commentId: UUID,
        val categoryId: UUID,
        val canceled: Boolean
) {
    constructor(l: LoggedTimeResult) : this(id = l.id, time = l.time, commentId = l.commentId, categoryId = l.categoryId, canceled = l.canceled)
}