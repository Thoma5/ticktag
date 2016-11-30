package io.ticktag.service.loggedtime.dto


import io.ticktag.restinterface.loggedtime.schema.CreateLoggedTimeJson
import java.time.Duration
import java.util.*

data class CreateLoggedTime(
        val time: Duration,
        var commentId: UUID?,
        val categoryId: UUID
) {
    constructor(l: CreateLoggedTimeJson) : this(time = l.time, commentId = l.commentId, categoryId = l.categoryId)
}