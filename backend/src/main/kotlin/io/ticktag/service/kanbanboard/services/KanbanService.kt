package io.ticktag.service.kanbanboard.services


import io.ticktag.service.Principal
import io.ticktag.service.kanbanboard.dto.KanbanBoardResult
import io.ticktag.service.kanbanboard.dto.KanbanColumnResult
import io.ticktag.service.kanbanboard.dto.UpdateKanbanColumn
import java.time.Instant
import java.util.*


interface KanbanService {
    /**
     * List all columns of a kanbanBoard with their tickets.
     * A column is equal to a Tag of a exclusive Tag group.
     * This list will be filtered acoriding to the paramters.
     */
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

    /**
     * List all available Kanbanboards of a project.
     * A kanbanboard is always an exclusive Tag Group
     */
    fun listBoards(projectId: UUID): List<KanbanBoardResult>

    /**
     * Updates a Column of the Kanbanboard
     * In the UpdateKanbanColumn will be a list of the tickets in a new sort.
     * For each action updateKanbanBoard has to be updated once.
     * If a ticket will be tragged to a new column, the id of the new column has to be provided.
     * @param column an encapsulated Object of the properties which will be updated
     * @param principal security principal
     * @param id id of the Tag/Columns
     */
    fun updateKanbanBoard(column: UpdateKanbanColumn, principal: Principal, id: UUID)

    /**
     * Get a KanbanBoard result
     */
    fun getBoard(boardId: UUID): KanbanBoardResult

    /**
     * For the given ticketId, all subtickets will be inserted below the specified ticket.
     */
    fun collectSubticket(ticketId: UUID, tagId: UUID, principal: Principal)
}