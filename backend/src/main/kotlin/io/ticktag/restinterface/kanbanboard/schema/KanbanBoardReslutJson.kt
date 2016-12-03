package io.ticktag.restinterface.kanbanboard.schema

import io.ticktag.service.tickettaggroup.dto.TicketTagGroupResult
import java.util.*

data class KanbanBoardReslutJson (
        val id: UUID,
        val name: String,
        val projectId: UUID
) {
    constructor(g: TicketTagGroupResult) : this(id = g.id, name = g.name, projectId = g.project_id)
}