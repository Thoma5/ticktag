package io.ticktag.service.tickettagrelation.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.tickettagrelation.dto.TicketTagRelationResult
import io.ticktag.service.tickettagrelation.services.TicketTagRelationService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*

@TicktagService
open class TicketTagRelationServiceImpl(
        private val tickets: TicketRepository,
        private val tags: TicketTagRepository
) : TicketTagRelationService {

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_RELATION)
    override fun getTicketTagRelation(@P("authTicketId") ticketId: UUID, tagId: UUID): TicketTagRelationResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignedTag = ticket.tags.find { it.id == tagId } ?: throw NotFoundException()

        return TicketTagRelationResult(ticketId = ticket.id, tagId = assignedTag.id)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_TAG_RELATION)
    override fun createOrGetIfExistsTicketTagRelation(@P("authTicketId") ticketId: UUID, tagId: UUID): TicketTagRelationResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val tag = tags.findOne(tagId) ?: throw NotFoundException()

        val assignedTag = ticket.tags.find { it.id == tagId }
        if (assignedTag == null) {
            ticket.tags.add(tag)
        }

        return TicketTagRelationResult(ticketId = ticket.id, tagId = tag.id)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_TAG_RELATION)
    override fun deleteTicketTagRelation(@P("authTicketId") ticketId: UUID, tagId: UUID) {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()

        val assignedTag = ticket.tags.find { it.id == tagId } ?: throw NotFoundException()
        ticket.tags.remove(assignedTag)
    }
}