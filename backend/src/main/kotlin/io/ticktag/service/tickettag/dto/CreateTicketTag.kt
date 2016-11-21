package io.ticktag.service.tickettag.dto

import io.ticktag.persistence.ticket.entity.TicketTag
import javax.validation.constraints.Pattern

data class CreateTicketTag(
        @field:Pattern(regexp = TicketTag.NAME_REGEX) val name: String,
        @field:Pattern(regexp = TicketTag.COLOR_REGEX) val color: String, //RGB HEX Color
        val order: Int
)