package io.ticktag.service.tickettaggroup.dto

import io.ticktag.persistence.ticket.entity.TicketTagGroup
import java.util.*

data class TicketTagGroupResult (
    val id: UUID,
    val name: String,
    val exclusive: Boolean,
    val project_id: UUID,
    val default_ticket_tag_id: UUID?,
    val required: Boolean
) {
    constructor(g: TicketTagGroup) : this(id = g.id, name = g.name, exclusive = g.exclusive, project_id = g.project.id, default_ticket_tag_id = g.defaultTicketTag?.id, required = g.required)
}