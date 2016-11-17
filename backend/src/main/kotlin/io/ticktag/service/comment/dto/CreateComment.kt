package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Instant
import java.util.*
import javax.validation.constraints.Size

data class CreateComment(
        @field:Size(min = 1, max = 500) val text: String,
        val ticketID: UUID
) {

}