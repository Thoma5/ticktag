package io.ticktag.service.tickettagrelation.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.kanban.KanbanCellRepository
import io.ticktag.persistence.ticket.TicketEventRepository
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.TicketEventTagAdded
import io.ticktag.persistence.ticket.entity.TicketEventTagRemoved
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.tickettagrelation.dto.TicketTagRelationResult
import io.ticktag.service.tickettagrelation.services.TicketTagRelationService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*

@TicktagService
open class TicketTagRelationServiceImpl(
        private val tickets: TicketRepository,
        private val tags: TicketTagRepository,
        private val ticketEvents: TicketEventRepository,
        private val users: UserRepository,
        private val kanbanCellRepository: KanbanCellRepository
) : TicketTagRelationService {

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_RELATION)
    override fun getTicketTagRelation(@P("authTicketId") ticketId: UUID, tagId: UUID): TicketTagRelationResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignedTag = ticket.tags.find { it.id == tagId } ?: throw NotFoundException()

        return TicketTagRelationResult(ticketId = ticket.id, tagId = assignedTag.id)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_TAG_RELATION)
    override fun createOrGetIfExistsTicketTagRelation(@P("authTicketId") ticketId: UUID, tagId: UUID, principal: Principal): TicketTagRelationResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val tag = tags.findOne(tagId) ?: throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()

        if (tag.ticketTagGroup.project.id != ticket.project.id) {
            throw NotFoundException()
        }

        val assignedTag = ticket.tags.find { it.id == tagId }
        if (assignedTag == null) {
            if (tag.ticketTagGroup.exclusive) {
                // Remove all tags from the same group
                val deletedTags = ticket.tags.filter { it in tag.ticketTagGroup.ticketTags }.toMutableList()
                ticket.kanbanCells.filter { it.tag in deletedTags }.forEach { kanbanCellRepository.delete(it) }
                deletedTags.forEach {
                    ticketEvents.insert(TicketEventTagRemoved.create(ticket, user, it))
                }
                ticket.tags = ticket.tags.filter { it !in tag.ticketTagGroup.ticketTags }.toMutableList()
            }
            ticketEvents.insert(TicketEventTagAdded.create(ticket, user, tag))
            ticket.tags.add(tag)
        }

        return TicketTagRelationResult(ticketId = ticket.id, tagId = tag.id)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_TAG_RELATION)
    override fun deleteTicketTagRelation(@P("authTicketId") ticketId: UUID, tagId: UUID, principal: Principal) {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()

        val assignedTag = ticket.tags.find { it.id == tagId } ?: throw NotFoundException()
        val kanbanCell = ticket.kanbanCells.find { it.tag.id == tagId }
        ticketEvents.insert(TicketEventTagRemoved.create(ticket, user, assignedTag))
        ticket.tags.remove(assignedTag)
        if (kanbanCell != null) {
            kanbanCellRepository.delete(kanbanCell)
        }
    }
}