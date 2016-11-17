package io.ticktag.service.tickettaggroup.dto

import java.util.*
import javax.validation.constraints.Size

data class UpdateTicketTagGroup (
    @field:Size(min = 3, max = 30) val name: String?,
    val exclusive: Boolean?,
    val default_ticket_tag_id: UUID?
)