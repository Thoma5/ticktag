package io.ticktag.service.comment.dto

import io.ticktag.service.loggedtime.dto.CreateLoggedTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Size

data class UpdateComment(
        @field:Size(min = 1, max = 20000) val text: String?,
        val mentionedTicketIds: List<UUID>?,
        @Valid val loggedTime: List<CreateLoggedTime>?
)
