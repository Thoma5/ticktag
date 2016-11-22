package io.ticktag.restinterface.tickettaggroup.schema

import java.util.*

data class UpdateTicketTagGroupRequestJson(
        val name: String?,
        val exclusive: Boolean?,
        val defaultTicketTagId: UUID?
)
