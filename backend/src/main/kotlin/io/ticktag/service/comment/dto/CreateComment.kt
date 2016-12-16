package io.ticktag.service.comment.dto

import io.ticktag.service.command.dto.Command
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Size

data class CreateComment(
        @field:Size(min = 1, max = 20000) val text: String,
        val ticketId: UUID,
        @Valid val commands: List<Command>
)



