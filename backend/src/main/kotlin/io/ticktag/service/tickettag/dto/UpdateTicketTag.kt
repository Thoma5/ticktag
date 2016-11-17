package io.ticktag.service.tickettag.dto

import javax.validation.constraints.Size

data class UpdateTicketTag(
    @field:Size(min = 3, max = 30) val name: String?,
    @field:Size(min = 6, max = 6) val color: String?,
    val order: Int?
)