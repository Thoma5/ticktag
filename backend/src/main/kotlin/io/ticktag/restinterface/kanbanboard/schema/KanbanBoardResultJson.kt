package io.ticktag.restinterface.kanbanboard.schema

import io.ticktag.service.kanbanboard.dto.KanbanBoardResult
import java.util.*

data class KanbanBoardResultJson(
        val id: UUID,
        val name: String,
        val projectId: UUID
) {
    constructor(g: KanbanBoardResult) : this(id = g.id, name = g.name, projectId = g.projectId)
}