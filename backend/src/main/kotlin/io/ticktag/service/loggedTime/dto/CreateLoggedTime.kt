package io.ticktag.service.loggedTime.dto


import io.ticktag.service.comment.dto.CreateLoggedTimeJson
import java.time.Duration
import java.util.*

data class CreateLoggedTime(
        val time: Duration,
        var commentId: UUID?,
        val categoryId: UUID
) {
    constructor(l: CreateLoggedTimeJson) : this(time = l.time, commentId = l.commentId, categoryId = l.categoryId)
}