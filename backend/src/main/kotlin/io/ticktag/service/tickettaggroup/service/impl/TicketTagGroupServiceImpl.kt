package io.ticktag.service.tickettaggroup.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.TicketTag
import io.ticktag.persistence.ticket.entity.TicketTagGroup
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.NotFoundException
import io.ticktag.service.tickettaggroup.dto.CreateTicketTagGroup
import io.ticktag.service.tickettaggroup.dto.TicketTagGroupResult
import io.ticktag.service.tickettaggroup.dto.UpdateTicketTagGroup
import io.ticktag.service.tickettaggroup.service.TicketTagGroupService
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

//TODO: Check Auth
@TicktagService
open class TicketTagGroupServiceImpl @Inject constructor(
        private val ticketTagGroups: TicketTagGroupRepository,
        private val ticketTags: TicketTagRepository,
        private val projects: ProjectRepository
) : TicketTagGroupService {

    override fun getTicketTagGroup(id: UUID): TicketTagGroupResult? {
        return TicketTagGroupResult(ticketTagGroups.findOne(id) ?: throw NotFoundException())
    }

    override fun listTicketTagGroups(projectId: UUID): List<TicketTagGroupResult> {
        val project = projects.findOne(projectId) ?: throw NotFoundException()
        return project.ticketTagGroups.map(::TicketTagGroupResult)
    }

    override fun createTicketTagGroup(@Valid ticketTagGroup: CreateTicketTagGroup): TicketTagGroupResult {
        val project = projects.findOne(ticketTagGroup.projectId) ?: throw NotFoundException()
        var defaultTicketTag: TicketTag? = null
        if (ticketTagGroup.defaultTicketTagId != null) {
            defaultTicketTag = ticketTags.findOne(ticketTagGroup.defaultTicketTagId) ?: throw NotFoundException()
        }
        val newTicketTagGroup = TicketTagGroup.create(ticketTagGroup.name, ticketTagGroup.exclusive, project, defaultTicketTag)
        return TicketTagGroupResult(newTicketTagGroup)
    }

    override fun deleteTicketTagGroup(id: UUID) {
        val ticketTagGroup = ticketTagGroups.findOne(id) ?: throw NotFoundException()
        ticketTagGroups.delete(ticketTagGroup)
    }

    override fun updateTicketTagGroup(id: UUID, @Valid ticketTagGroup: UpdateTicketTagGroup): TicketTagGroupResult {
        val ticketTagGroupToUpdate = ticketTagGroups.findOne(id) ?: throw NotFoundException()
        if (ticketTagGroup.name != null) {
            ticketTagGroupToUpdate.name = ticketTagGroup.name
        }
        if (ticketTagGroup.exclusive != null) {
            ticketTagGroupToUpdate.exclusive = ticketTagGroup.exclusive
        }
        if (ticketTagGroup.default_ticket_tag_id != null) {
            val ticketTag = ticketTags.findOne(ticketTagGroup.default_ticket_tag_id) ?: throw NotFoundException()
            ticketTagGroupToUpdate.defaultTicketTag = ticketTag
        }
        return TicketTagGroupResult(ticketTagGroupToUpdate)
    }

}