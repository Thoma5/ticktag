package io.ticktag.restinterface.tickettag.schema

import com.sun.org.apache.xpath.internal.operations.Bool
import io.ticktag.service.tickettag.dto.TicketTagResult
import java.util.*

data class TicketTagResultJson(
        val id: UUID,
        val name: String,
        val normalizedName: String,
        val color: String,
        val order: Int,
        val ticketTagGroupId: UUID,
        val disabled: Boolean,
        val autoClose: Boolean
) {
    constructor(t: TicketTagResult) : this(id = t.id, name = t.name, normalizedName = t.normalizedName, color = t.color, order = t.order, ticketTagGroupId = t.ticketTagGroupId, disabled = t.disabled,autoClose = t.autoClose)
}