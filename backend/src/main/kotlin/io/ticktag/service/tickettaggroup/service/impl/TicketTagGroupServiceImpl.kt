package io.ticktag.service.tickettaggroup.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.TicketTagGroup
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.*
import io.ticktag.service.tickettaggroup.dto.CreateTicketTagGroup
import io.ticktag.service.tickettaggroup.dto.TicketTagGroupResult
import io.ticktag.service.tickettaggroup.dto.UpdateTicketTagGroup
import io.ticktag.service.tickettaggroup.service.TicketTagGroupService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class TicketTagGroupServiceImpl @Inject constructor(
        private val ticketTagGroups: TicketTagGroupRepository,
        private val ticketTags: TicketTagRepository,
        private val projects: ProjectRepository
) : TicketTagGroupService {

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_GROUP)
    override fun getTicketTagGroup(@P("authTicketTagGroupId") id: UUID): TicketTagGroupResult? {
        return TicketTagGroupResult(ticketTagGroups.findOne(id) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTicketTagGroups(@P("authProjectId") projectId: UUID): List<TicketTagGroupResult> {
        val project = projects.findOne(projectId) ?: throw NotFoundException()
        return project.ticketTagGroups.map(::TicketTagGroupResult)
    }

    @PreAuthorize(AuthExpr.CREATE_TICKET_TAG_GROUP)
    override fun createTicketTagGroup(@Valid ticketTagGroup: CreateTicketTagGroup, @P("authProjectId") projectId: UUID): TicketTagGroupResult {
        val project = projects.findOne(projectId) ?: throw NotFoundException()
        val newTicketTagGroup = TicketTagGroup.create(ticketTagGroup.name, ticketTagGroup.exclusive, project)
        ticketTagGroups.insert(newTicketTagGroup)
        return TicketTagGroupResult(newTicketTagGroup)
    }

    @PreAuthorize(AuthExpr.EDIT_TICKET_TAG_GROUP)
    override fun deleteTicketTagGroup(@P("authTicketTagGroupId") id: UUID) {
        val ticketTagGroup = ticketTagGroups.findOne(id) ?: throw NotFoundException()
        ticketTagGroups.delete(ticketTagGroup)
    }

    @PreAuthorize(AuthExpr.EDIT_TICKET_TAG_GROUP)
    override fun updateTicketTagGroup(@P("authTicketTagGroupId") id: UUID, @Valid ticketTagGroup: UpdateTicketTagGroup): TicketTagGroupResult {
        val ticketTagGroupToUpdate = ticketTagGroups.findOne(id) ?: throw NotFoundException()
        if (ticketTagGroup.name != null) {
            ticketTagGroupToUpdate.name = ticketTagGroup.name
        }
        if (ticketTagGroup.exclusive != null) {
            ticketTagGroupToUpdate.exclusive = ticketTagGroup.exclusive
        }
        if (ticketTagGroup.default_ticket_tag_id != null) {
            val ticketTag = ticketTags.findOne(ticketTagGroup.default_ticket_tag_id) ?: throw NotFoundException()
            if (ticketTagGroupToUpdate.id != ticketTag.ticketTagGroup.id) {
                throw TicktagValidationException(listOf(ValidationError("updateTicketTagGroup.defaultTicketTag", ValidationErrorDetail.Other("not in group"))))
            }
            ticketTagGroupToUpdate.defaultTicketTag = ticketTag
        }
        return TicketTagGroupResult(ticketTagGroupToUpdate)
    }

}