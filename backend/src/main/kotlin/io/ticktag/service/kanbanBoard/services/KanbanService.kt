package io.ticktag.service.kanbanBoard.services


import io.ticktag.service.Principal
import io.ticktag.service.kanbanBoard.dto.KanbanBoardResult
import io.ticktag.service.kanbanBoard.dto.KanbanColumnResult
import io.ticktag.service.kanbanBoard.dto.UpdateKanbanColumn
import java.util.*


interface KanbanService {
    fun listColumns(kanbanBoardId: UUID): List<KanbanColumnResult>
    fun listBoards(projectId: UUID): List<KanbanBoardResult>
    fun updateKanbanBoard(columns: List<UpdateKanbanColumn>, principal: Principal): List<KanbanColumnResult>
}