package io.ticktag.service.tickettaggroup.dto

import java.util.*

data class CreateTicketTagGroup(
        val name: String,
        val exclusive: Boolean,
        val projectId: UUID,
        val defaultTicketTagId: UUID?
)