package io.ticktag.service.kanbanboard.dto

import io.ticktag.restinterface.kanbanboard.schema.UpdateKanbanColumnJson
import java.util.*

data class UpdateKanbanColumn(
        val id: UUID,
        val ticketIds: List<UUID>,
        val ticketIdToUpdate: UUID
) {
    constructor(g: UpdateKanbanColumnJson) : this(id = g.id, ticketIds = g.ticketIds,ticketIdToUpdate = g.ticketIdToUpdate)
}
