package io.ticktag.service.kanbanboard.services


import io.ticktag.service.Principal
import io.ticktag.service.kanbanboard.dto.KanbanBoardResult
import io.ticktag.service.kanbanboard.dto.KanbanColumnResult
import io.ticktag.service.kanbanboard.dto.UpdateKanbanColumn
import java.time.Instant
import java.util.*


interface KanbanService {
    fun listColumns(kanbanBoardId: UUID,
                    numbers: List<Int>?,
                    title: String?,
                    tags: List<String>?,
                    users: List<String>?,
                    progressOne: Float?,
                    progressTwo: Float?,
                    progressGreater: Boolean?,
                    dueDateOne: Instant?,
                    dueDateTwo: Instant?,
                    dueDateGreater: Boolean?,
                    storyPointsOne: Int?,
                    storyPointsTwo: Int?,
                    storyPointsGreater: Boolean?,
                    open: Boolean?,
                    parent: Int?): List<KanbanColumnResult>

    fun listBoards(projectId: UUID): List<KanbanBoardResult>
    fun updateKanbanBoard(columns: UpdateKanbanColumn, principal: Principal, id: UUID)
    fun getBoard(boardId: UUID): KanbanBoardResult
    fun collectSubticket(ticketId: UUID, tagId: UUID, principal: Principal)
}