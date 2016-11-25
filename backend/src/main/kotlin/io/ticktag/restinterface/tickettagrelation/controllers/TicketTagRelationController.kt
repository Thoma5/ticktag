package io.ticktag.restinterface.tickettagrelation.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.tickettagrelation.schema.TicketTagRelationResultJson
import io.ticktag.restinterface.ticketuserrelation.schema.TickerUserRelationResultJson
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import io.ticktag.service.tickettagrelation.services.TicketTagRelationService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/ticket")
@Api(tags = arrayOf("tickettagrelation"), description = "ticket/tag relation")
open class TicketTagRelationController @Inject constructor(
        private val ticketTagRelationService: TicketTagRelationService
) {
    @GetMapping(value = "{ticketId}/tag/{tagId}")
    open fun getTicketTagRelation(
            @PathVariable ticketId: UUID,
            @PathVariable tagId: UUID): TicketTagRelationResultJson {
        return TicketTagRelationResultJson(ticketTagRelationService.getTicketTagRelation(ticketId, tagId))
    }

    @PutMapping(value = "{ticketId}/tag/{tagId}")
    open fun setTicketTagRelation(
            @PathVariable ticketId: UUID,
            @PathVariable tagId: UUID): TicketTagRelationResultJson {
        return TicketTagRelationResultJson(ticketTagRelationService.createOrGetIfExistsTicketTagRelation(ticketId, tagId))
    }

    @DeleteMapping(value = "{ticketId}/tag/{tagId}")
    open fun deleteTicketTagRelation(
            @PathVariable ticketId: UUID,
            @PathVariable tagId: UUID) {
        ticketTagRelationService.deleteTicketTagRelation(ticketId, tagId)
    }

}