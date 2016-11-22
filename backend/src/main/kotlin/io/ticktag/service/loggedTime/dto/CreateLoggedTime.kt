package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.Size

data class CreateLoggedTime(
        val time:Duration,
        val commentId: UUID,
        val categoryId: UUID
) {

}