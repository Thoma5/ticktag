package io.ticktag.service.tickettag.services.impl

import io.ticktag.TicktagService
import io.ticktag.library.unicode.NameNormalizationLibrary
import io.ticktag.persistence.ticket.entity.TicketTag
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.service.*
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
        private val ticketTags: TicketTagRepository,
        private val nameNormalizationLibrary: NameNormalizationLibrary
) : TicketTagService {

    @PreAuthorize(AuthExpr.READ_TICKET_TAG)
    override fun getTicketTag(@P("authTicketTagId") id: UUID): TicketTagResult {
        return TicketTagResult(ticketTags.findOne(id) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.READ_TICKET_TAG_FOR_GROUP)
    override fun listTicketTagsInGroup(@P("authTicketTagGroupId") ticketTagGroupId: UUID): List<TicketTagResult> {
        val ticketTags = ticketTags.findByTicketTagGroupIdAndDisabledOrderByOrderAsc(ticketTagGroupId, false)
        return ticketTags.map(::TicketTagResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTicketTagsInProject(@P("authProjectId") projectId: UUID): List<TicketTagResult> {
        val ticketTags = ticketTags.findByProjectId(projectId)
        return ticketTags.map(::TicketTagResult)
    }

    @PreAuthorize(AuthExpr.CREATE_TICKET_TAG)
    override fun createTicketTag(@Valid ticketTag: CreateTicketTag, @P("authTicketTagGroupId") ticketTagGroupId: UUID): TicketTagResult {
        val ticketTagGroup = ticketTagGroups.findOne(ticketTagGroupId) ?: throw NotFoundException()

        val normalizedName = nameNormalizationLibrary.normalize(ticketTag.name)
        if (ticketTags.findByNormalizedNameAndProjectId(normalizedName, ticketTagGroup.project.id) != null) {
            throw TicktagValidationException(listOf(ValidationError("createTicketTag.name", ValidationErrorDetail.Other("inuse"))))
        }

        val newTicketTag = TicketTag.create(ticketTag.name, normalizedName, ticketTag.color, ticketTag.order, ticketTagGroup)
        ticketTags.insert(newTicketTag)
        return TicketTagResult(newTicketTag)
    }

    @PreAuthorize(AuthExpr.EDIT_TICKET_TAG)
    override fun deleteTicketTag(@P("authTicketTagId") id: UUID) {
        val ticketTag = ticketTags.findOne(id) ?: throw NotFoundException()
        ticketTag.normalizedName = ""
        ticketTag.disabled = true
    }

    @PreAuthorize(AuthExpr.EDIT_TICKET_TAG)
    override fun updateTicketTag(@P("authTicketTagId") id: UUID, @Valid ticketTag: UpdateTicketTag): TicketTagResult {
        val ticketTagToUpdate = ticketTags.findOne(id) ?: throw NotFoundException()
        if (ticketTag.name != null) {
            val normalizedName = nameNormalizationLibrary.normalize(ticketTag.name)
            val foundTag = ticketTags.findByNormalizedNameAndProjectId(normalizedName, ticketTagToUpdate.ticketTagGroup.project.id)
            if (foundTag != null && foundTag.id != ticketTagToUpdate.id) {
                throw TicktagValidationException(listOf(ValidationError("updateTicketTag.name", ValidationErrorDetail.Other("inuse"))))
            }
            ticketTagToUpdate.name = ticketTag.name
            ticketTagToUpdate.normalizedName = normalizedName
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