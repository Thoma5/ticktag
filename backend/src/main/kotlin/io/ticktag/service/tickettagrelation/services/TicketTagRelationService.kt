package io.ticktag.service.tickettagrelation.services

import io.ticktag.service.Principal
import io.ticktag.service.tickettagrelation.dto.TicketTagRelationResult
import java.util.*

interface TicketTagRelationService {
    fun getTicketTagRelation(ticketId: UUID, tagId: UUID): TicketTagRelationResult
    fun createOrGetIfExistsTicketTagRelation(ticketId: UUID, tagId: UUID, principal: Principal): TicketTagRelationResult
    fun deleteTicketTagRelation(ticketId: UUID, tagId: UUID, principal: Principal)
}