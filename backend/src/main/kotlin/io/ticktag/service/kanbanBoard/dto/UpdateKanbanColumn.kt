package io.ticktag.service.kanbanBoard.dto

import io.ticktag.restinterface.kanbanboard.schema.UpdateKanbanColumnJson
import java.util.*

data class UpdateKanbanColumn (
        val id: UUID,
        val ticketIds: List<UUID>
){
    constructor(g: UpdateKanbanColumnJson) : this(id = g.id, ticketIds = g.ticketIds)
}
