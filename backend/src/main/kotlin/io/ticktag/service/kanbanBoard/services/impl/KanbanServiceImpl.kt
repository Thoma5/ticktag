package io.ticktag.service.kanbanBoard.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.kanban.KanbanCellRepository
import io.ticktag.persistence.kanban.entity.KanbanCell
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.kanbanBoard.dto.KanbanBoardResult
import io.ticktag.service.kanbanBoard.dto.KanbanColumnResult
import io.ticktag.service.kanbanBoard.dto.UpdateKanbanColumn
import io.ticktag.service.kanbanBoard.services.KanbanService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class KanbanServiceImpl @Inject constructor(
        private val ticketTagGroups: TicketTagGroupRepository,
        private val ticketTagRepository: TicketTagRepository,
        private val kanbanCellRepository: KanbanCellRepository,
        private val ticketRepository: TicketRepository
) : KanbanService {

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_FOR_GROUP)
    override fun listColumns(@P("authTicketTagGroupId") kanbanBoardId: UUID): List<KanbanColumnResult> {
        val columns = ticketTagRepository.findByTicketTagGroupIdOrderByOrderAsc(kanbanBoardId)
        var result = emptyList<KanbanColumnResult>().toMutableList()

        for (column in columns) {
            var tickets: MutableList<Ticket> = mutableListOf()
            val aktTickets = column.tickets
            val lastSort = kanbanCellRepository.findByTicketTagId(column.id)
            tickets.addAll(lastSort.filter { aktTickets.contains(it) })
            tickets.addAll(aktTickets.filter { !lastSort.contains(it) })
            kanbanCellRepository.deleteByTagId(column.id)

            var i = 0
            tickets.forEach {
                val kanbanCell = KanbanCell.create(it,column,i)
                kanbanCellRepository.insert(kanbanCell)
                i++
            }

            result.add(KanbanColumnResult(column, tickets.map { it.id }))
        }

        return result
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listBoards(@P("authProjectId") projectId: UUID): List<KanbanBoardResult> {
        return ticketTagGroups.findExclusiveTicketTagGroupsByProjectId(projectId).map(::KanbanBoardResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun updateKanbanBoard(columns: List<UpdateKanbanColumn>): List<KanbanColumnResult> {
        var result = emptyList<KanbanColumnResult>().toMutableList()
        for (column in columns) {
            kanbanCellRepository.deleteByTagId(column.id)
            var tag = ticketTagRepository.findOne(column.id)?:throw NotFoundException()
            var tickets = emptyList<Ticket>().toMutableList()
            if (column.ticketIds.size != 0) {

                var i = 0
                column.ticketIds.forEach {
                    val ticket = ticketRepository.findOne(it)?:throw NotFoundException()
                    tickets.add(ticket)
                    val kanbanCell = KanbanCell.create(ticket, tag, i)
                    kanbanCellRepository.insert(kanbanCell)
                    i++
                }
            }
            result.add(KanbanColumnResult(tag, tickets.map { it.id }))
        }
        return result
    }
}