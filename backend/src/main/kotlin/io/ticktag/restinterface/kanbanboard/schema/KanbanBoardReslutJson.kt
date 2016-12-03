package io.ticktag.restinterface.kanbanboard.schema

import io.ticktag.service.kanbanBoard.dto.KanbanBoardResult
import java.util.*

data class KanbanBoardReslutJson(
        val id: UUID,
        val name: String,
        val projectId: UUID,
        val columnIds: List<UUID>
) {
    constructor(g: KanbanBoardResult) : this(id = g.id, name = g.name, projectId = g.project_id, columnIds = g.columnIds)
}