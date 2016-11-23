package io.ticktag.service.comment.dto

import java.util.*
import javax.validation.constraints.Size

data class UpdateComment(
        @field:Size(min = 1, max = 500) val text: String?,
        val mentionedTicketIds: List<UUID>?,
        val loggedTime: List<CreateLoggedTime>?
) {}