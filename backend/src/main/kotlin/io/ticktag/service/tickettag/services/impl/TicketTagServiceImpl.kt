package io.ticktag.service.tickettag.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.entity.TicketTag
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.NotFoundException
import io.ticktag.service.tickettag.dto.CreateTicketTag
import io.ticktag.service.tickettag.dto.TicketTagResult
import io.ticktag.service.tickettag.dto.UpdateTicketTag
import io.ticktag.service.tickettag.services.TicketTagService
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

//TODO: Check Auth
@TicktagService
open class TicketTagServiceImpl @Inject constructor(
        private val ticketTagGroups: TicketTagGroupRepository,
        private val ticketTags: TicketTagRepository
) : TicketTagService {

    override fun getTicketTag(id: UUID): TicketTagResult? {
        return TicketTagResult(ticketTags.findOne(id) ?: throw NotFoundException())
    }

    override fun listTicketTags(ticketTagGroupID: UUID): List<TicketTagResult> {
        val ticketTagGroup = ticketTagGroups.findOne(ticketTagGroupID) ?: throw NotFoundException()
        return ticketTagGroup.ticketTags.map(::TicketTagResult)
    }

    override fun createTicketTag(@Valid ticketTag: CreateTicketTag): TicketTagResult {
        val ticketTagGroup = ticketTagGroups.findOne(ticketTag.ticketTagGroupId) ?: throw NotFoundException()
        val newTicketTag = TicketTag.create(ticketTag.name, ticketTag.color, ticketTag.order, ticketTagGroup)
        ticketTags.insert(newTicketTag)
        return TicketTagResult(newTicketTag)
    }

    override fun deleteTicketTag(id: UUID) {
        val ticketTag = ticketTags.findOne(id) ?: throw NotFoundException()
        ticketTags.delete(ticketTag)
    }

    override fun updateTicketTag(id: UUID, @Valid ticketTag: UpdateTicketTag): TicketTagResult {
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