package io.ticktag.service.kanbanBoard.dto

import io.ticktag.persistence.ticket.entity.TicketTagGroup
import java.util.*


data class KanbanBoardResult(
        val id: UUID,
        val name: String,
        val project_id: UUID
) {
    constructor(g: TicketTagGroup) : this(id = g.id, name = g.name, project_id = g.project.id)
}