package io.ticktag.restinterface.tickettag.schema

import java.util.*

data class CreateTicketTagRequestJson(
        val name: String,
        val color: String,
        val order: Int,
        val ticketTagGroupId: UUID,
        val autoClose: Boolean
)
