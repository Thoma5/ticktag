package io.ticktag.service.kanbanboard.dto

import io.ticktag.persistence.ticket.entity.TicketTagGroup
import java.util.*


data class KanbanBoardResult(
        val id: UUID,
        val name: String,
        val projectId: UUID
) {
    constructor(g: TicketTagGroup) : this(id = g.id, name = g.name, projectId = g.project.id)
}