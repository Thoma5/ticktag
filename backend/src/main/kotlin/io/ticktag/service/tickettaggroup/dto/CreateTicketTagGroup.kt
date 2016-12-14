package io.ticktag.service.tickettaggroup.dto

import javax.validation.constraints.Size

data class CreateTicketTagGroup(
        @field:Size(min = 3, max = 30) val name: String,
        val exclusive: Boolean
)