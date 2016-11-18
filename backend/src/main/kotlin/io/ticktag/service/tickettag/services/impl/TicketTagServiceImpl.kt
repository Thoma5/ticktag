package io.ticktag.service.tickettag.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.entity.TicketTag
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.tickettag.dto.CreateTicketTag
import io.ticktag.service.tickettag.dto.TicketTagResult
import io.ticktag.service.tickettag.dto.UpdateTicketTag
import io.ticktag.service.tickettag.services.TicketTagService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class TicketTagServiceImpl @Inject constructor(
        private val ticketTagGroups: TicketTagGroupRepository,
        private val ticketTags: TicketTagRepository
) : TicketTagService {

    @PreAuthorize(AuthExpr.READ_TICKET_TAG)
    override fun getTicketTag(@P("authTicketTagId") id: UUID): TicketTagResult {
        return TicketTagResult(ticketTags.findOne(id) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_FOR_GROUP)
    override fun listTicketTags(@P("authTicketTagGroupId") ticketTagGroupID: UUID): List<TicketTagResult> {
        val ticketTagGroup = ticketTagGroups.findOne(ticketTagGroupID) ?: throw NotFoundException()
        return ticketTagGroup.ticketTags.map(::TicketTagResult)
    }

    @PreAuthorize(AuthExpr.CREATE_TICKET_TAG)
    override fun createTicketTag(@Valid ticketTag: CreateTicketTag, @P("authTicketTagGroupId") ticketTagGroupId: UUID): TicketTagResult {
        val ticketTagGroup = ticketTagGroups.findOne(ticketTagGroupId) ?: throw NotFoundException()
        val newTicketTag = TicketTag.create(ticketTag.name, ticketTag.color, ticketTag.order, ticketTagGroup)
        ticketTags.insert(newTicketTag)
        return TicketTagResult(newTicketTag)
    }

    @PreAuthorize(AuthExpr.EDIT_TICKET_TAG)
    override fun deleteTicketTag(@P("authTicketTagId") id: UUID) {
        val ticketTag = ticketTags.findOne(id) ?: throw NotFoundException()
        ticketTags.delete(ticketTag)
    }

    @PreAuthorize(AuthExpr.EDIT_TICKET_TAG)
    override fun updateTicketTag(@P("authTicketTagId") id: UUID, @Valid ticketTag: UpdateTicketTag): TicketTagResult {
        val ticketTagToUpdate = ticketTags.findOne(id) ?: throw NotFoundException()
        if (ticketTag.name != null) {
            ticketTagToUpdate.name = ticketTag.name
        }
        if (ticketTag.color != null) {
            ticketTagToUpdate.color = ticketTag.color
        }
        if (ticketTag.order != null) {
            ticketTagToUpdate.order = ticketTag.order
        }
        return TicketTagResult(ticketTagToUpdate)
    }

}