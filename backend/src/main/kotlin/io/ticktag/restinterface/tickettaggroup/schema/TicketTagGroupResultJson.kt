package io.ticktag.restinterface.tickettaggroup.schema

import io.ticktag.service.tickettaggroup.dto.TicketTagGroupResult
import java.util.*

data class TicketTagGroupResultJson(
        val id: UUID,
        val name: String,
        val exclusive: Boolean,
        val projectId: UUID,
        val defaultTicketTagId: UUID?,
        val required: Boolean
) {
    constructor(g: TicketTagGroupResult) : this(id = g.id, name = g.name, exclusive = g.exclusive, projectId = g.project_id, defaultTicketTagId = g.default_ticket_tag_id, required = g.required)
}