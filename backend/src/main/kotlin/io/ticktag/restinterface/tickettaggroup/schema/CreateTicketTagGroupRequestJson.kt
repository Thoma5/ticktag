package io.ticktag.restinterface.tickettaggroup.schema

import java.util.*

data class CreateTicketTagGroupRequestJson(
        val name: String,
        val exclusive: Boolean,
        val projectId: UUID
)
