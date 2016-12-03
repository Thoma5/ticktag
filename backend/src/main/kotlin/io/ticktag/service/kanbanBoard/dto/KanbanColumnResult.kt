package io.ticktag.service.kanbanBoard.dto

import io.ticktag.persistence.ticket.entity.TicketTag
import java.util.*


data class KanbanColumnResult (
        val id: UUID,
        val name: String,
        val normalizedName: String,
        val color: String,
        val order: Int,
        val kanbanBoardId: UUID,
        val ticketIds: List<UUID>
) {
    constructor(t: TicketTag) : this(id = t.id, name = t.name, normalizedName = t.normalizedName, color = t.color, order = t.order, kanbanBoardId = t.ticketTagGroup.id, ticketIds = t.tickets.map { it.id })
}