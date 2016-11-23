package io.ticktag.service.comment.dto

import io.ticktag.service.loggedTime.dto.CreateLoggedTime
import java.util.*
import javax.validation.constraints.Size

data class CreateComment(
        @field:Size(min = 1, max = 500) val text: String,
        val ticketId: UUID,
        val mentionedTicketIds: List<UUID>,
        val loggedTime: List<CreateLoggedTime>
)



