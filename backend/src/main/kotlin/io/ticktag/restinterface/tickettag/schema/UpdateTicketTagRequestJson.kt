package io.ticktag.restinterface.tickettag.schema

data class UpdateTicketTagRequestJson(
        val name: String?,
        val color: String?,
        val order: Int?
)
