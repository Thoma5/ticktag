package io.ticktag.restinterface.kanbanboard.schema

import io.ticktag.service.kanbanBoard.dto.KanbanColumnResult
import java.util.*

data class KanbanColumnResultJson (
        val id: UUID,
        val name: String,
        val normalizedName: String,
        val color: String,
        val order: Int,
        val kanbanBoardId: UUID,
        val ticketIds: List<UUID>
) {
    constructor(t: KanbanColumnResult) : this(id = t.id, name = t.name, normalizedName = t.normalizedName, color = t.color, order = t.order, kanbanBoardId = t.kanbanBoardId, ticketIds = t.ticketIds)
}