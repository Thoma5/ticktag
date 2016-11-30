package io.ticktag.restinterface.tickettag.schema

import io.ticktag.service.tickettag.dto.TicketTagResult
import java.util.*

data class TicketTagResultJson(
        val id: UUID,
        val name: String,
        val normalizedName: String,
        val color: String,
        val order: Int,
        val ticketTagGroupId: UUID
) {
    constructor(t: TicketTagResult) : this(id = t.id, name = t.name, normalizedName = t.normalizedName, color = t.color, order = t.order, ticketTagGroupId = t.ticketTagGroupId)
}