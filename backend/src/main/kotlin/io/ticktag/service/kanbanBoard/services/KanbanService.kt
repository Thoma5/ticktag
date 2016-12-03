package io.ticktag.service.kanbanBoard.services


import io.ticktag.service.kanbanBoard.dto.KanbanBoardResult
import io.ticktag.service.kanbanBoard.dto.KanbanColumnResult
import io.ticktag.service.tickettaggroup.dto.TicketTagGroupResult
import java.util.*


interface KanbanService {
    fun listColumns(kanbanBoardId: UUID): List<KanbanColumnResult>
    fun listBoards(projectId: UUID): List<KanbanBoardResult>
}