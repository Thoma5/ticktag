package io.ticktag.service.tickettagrelation.services

import io.ticktag.service.Principal
import io.ticktag.service.tickettagrelation.dto.TicketTagRelationResult
import java.util.*

interface TicketTagRelationService {
    /**
     * Get relation of a Ticket to a Tag ( It is an assignment of a Tag to a ticket.)
     */
    fun getTicketTagRelation(ticketId: UUID, tagId: UUID): TicketTagRelationResult

    /**
     * Assign a tag to a ticket if this relation doesn't exist.
     */
    fun createOrGetIfExistsTicketTagRelation(ticketId: UUID, tagId: UUID, principal: Principal): TicketTagRelationResult

    /**
     * Deletes one sssignment of a tag to a ticket.
     */
    fun deleteTicketTagRelation(ticketId: UUID, tagId: UUID, principal: Principal)
}