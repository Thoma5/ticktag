package io.ticktag.service.tickettag.dto

import io.ticktag.persistence.ticket.entity.TicketTag
import java.util.*

data class TicketTagResult(
        val id: UUID,
        val name: String,
        val normalizedName: String,
        val color: String,
        val order: Int,
        val ticketTagGroupId: UUID,
        val disabled: Boolean,
        val autoClose: Boolean
) {
    constructor(t: TicketTag) : this(id = t.id, name = t.name, normalizedName = t.normalizedName, color = t.color, order = t.order, ticketTagGroupId = t.ticketTagGroup.id, disabled = t.disabled, autoClose = t.autoClose)
}