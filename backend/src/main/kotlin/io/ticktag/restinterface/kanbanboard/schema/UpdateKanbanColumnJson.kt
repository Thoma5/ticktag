package io.ticktag.restinterface.kanbanboard.schema

import java.util.*


data class UpdateKanbanColumnJson (
    val id: UUID,
    val ticketIds: List<UUID>,
    val ticketIdToUpdate: UUID
)