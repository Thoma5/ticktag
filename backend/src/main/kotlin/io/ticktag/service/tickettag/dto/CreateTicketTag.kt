package io.ticktag.service.tickettag.dto

import io.ticktag.persistence.ticket.entity.TicketTag
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateTicketTag(
        @field:Size(min = 1, max = 30) val name: String,
        @field:Pattern(regexp = TicketTag.COLOR_REGEX) val color: String, //RGB HEX Color
        val order: Int
)