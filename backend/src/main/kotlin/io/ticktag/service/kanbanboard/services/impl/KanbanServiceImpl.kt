package io.ticktag.service.kanbanboard.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.kanban.KanbanCellRepository
import io.ticktag.persistence.kanban.entity.KanbanCell
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.dto.TicketFilter
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.*
import io.ticktag.service.kanbanboard.dto.KanbanBoardResult
import io.ticktag.service.kanbanboard.dto.KanbanColumnResult
import io.ticktag.service.kanbanboard.dto.UpdateKanbanColumn
import io.ticktag.service.kanbanboard.services.KanbanService
import io.ticktag.service.tickettagrelation.services.TicketTagRelationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Instant
import java.util.*
import javax.inject.Inject

@TicktagService
open class KanbanServiceImpl @Inject constructor(
        private val ticketTagGroups: TicketTagGroupRepository,
        private val ticketTagRepository: TicketTagRepository,
        private val kanbanCellRepository: KanbanCellRepository,
        private val ticketRepository: TicketRepository,
        private val ticketTagRelationService: TicketTagRelationService
) : KanbanService {

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_FOR_GROUP)
    override fun listColumns(@P("authTicketTagGroupId") kanbanBoardId: UUID,
                             number: Int?,
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
                             open: Boolean?): List<KanbanColumnResult> {

        val columns = ticketTagRepository.findByTicketTagGroupIdOrderByOrderAsc(kanbanBoardId)
        var result = emptyList<KanbanColumnResult>().toMutableList()
        val filter = TicketFilter(columns.first().ticketTagGroup.project.id, number, title, tags, users, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo, storyPointsGreater, open)
        val filteredTickets = ticketRepository.findAll(filter)
        val ascOrder = Sort.Direction.ASC
        val sortOrder = Sort.Order(ascOrder, "order")
        val pageRequest = PageRequest(0, 50, Sort(sortOrder))
        for (column in columns) {
            var tickets: MutableList<Ticket> = mutableListOf()
            val aktTickets = column.tickets
            val lastSort = kanbanCellRepository.findByTicketTagId(column.id, pageRequest)
            tickets.addAll(lastSort.filter { aktTickets.contains(it) })
            tickets.addAll(aktTickets.filter { !lastSort.contains(it) })
            tickets = tickets.filter { t -> filteredTickets.contains(t) }.toMutableList()

            result.add(KanbanColumnResult(column, tickets.map { it.id }))
        }

        return result
    }
    @PreAuthorize(AuthExpr.READ_TICKET_TAG)
    override fun collecSubticket(ticketId: UUID,@P("authTicketTagId") tagId: UUID, principal: Principal) {
        val ticket = ticketRepository.findOne(ticketId) ?:throw NotFoundException()
        val tag = ticketTagRepository.findOne(tagId) ?:throw NotFoundException()
        if (!ticket.tags.map { it.id }.contains(tagId)) throw NotFoundException()
        for (subticket in ticket.subTickets){
            ticketTagRelationService.createOrGetIfExistsTicketTagRelation(subticket.id,tagId,principal)
        }
        val ascOrder = Sort.Direction.ASC
        val sortOrder = Sort.Order(ascOrder, "order")
        val pageRequest = PageRequest(0, 50, Sort(sortOrder))
        val lastSort = kanbanCellRepository.findByTicketTagId(tagId,pageRequest)
        val newSort = lastSort.filter {  !ticket.subTickets.contains(it) }.toMutableList()
        newSort.addAll(newSort.indexOf(ticket),ticket.subTickets)
        var i =0
        kanbanCellRepository.deleteByTagId(tagId)
        newSort.forEach {
            val kanbanCell = KanbanCell.create(ticket,tag,i)
            i++
        }

    }

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_GROUP)
    override fun getBoard(@P("authTicketTagGroupId") boardId: UUID): KanbanBoardResult = KanbanBoardResult(ticketTagGroups.findOne(boardId) ?: throw NotFoundException())

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listBoards(@P("authProjectId") projectId: UUID): List<KanbanBoardResult> = ticketTagGroups.findExclusiveTicketTagGroupsByProjectId(projectId).map(::KanbanBoardResult)

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun updateKanbanBoard(columns: List<UpdateKanbanColumn>, principal: Principal) {
        for (column in columns) {
            kanbanCellRepository.deleteByTagId(column.id)
            var tag = ticketTagRepository.findOne(column.id) ?: throw NotFoundException()
            if (column.ticketIds.size != 0) {
                var i = 0
                column.ticketIds.forEach {
                    val ticket = ticketRepository.findOne(it) ?: throw NotFoundException()
                    if (tag.tickets.contains(ticket)) {
                        val kanbanCell = KanbanCell.create(ticket, tag, i)
                        kanbanCellRepository.insert(kanbanCell)
                        i++
                    }
                }
            }

        }
    }
}