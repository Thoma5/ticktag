package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.Size

data class CreateLoggedTime(
        val time: Duration,
        var commentId: UUID?,
        val categoryId: UUID
) {
    constructor(l: CreateLoggedTimeJson) : this(time = l.time, commentId = l.commentId, categoryId = l.categoryId)
}