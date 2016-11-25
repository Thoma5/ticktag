package io.ticktag.restinterface.ticketuserrelation.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticketuserrelation.schema.TickerUserRelationResultJson
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/ticket")
@Api(tags = arrayOf("ticketuserrelation"), description = "ticket/tag/user relation")
open class TicketUserRelationController @Inject constructor(
        private val ticketAssignmentService: TicketAssignmentService
) {
    @GetMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun getTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable assignmentTagId: UUID,
            @PathVariable userId: UUID): TickerUserRelationResultJson {
        val ticketAssignment = ticketAssignmentService.getTicketAssignment(ticketId, assignmentTagId, userId)
        return TickerUserRelationResultJson(ticketAssignment)
    }

    @PostMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun createTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable assignmentTagId: UUID,
            @PathVariable userId: UUID): TickerUserRelationResultJson {
        val ticketAssignment = ticketAssignmentService.createTicketAssignment(ticketId, assignmentTagId, userId)
        return TickerUserRelationResultJson(ticketAssignment)
    }

    @DeleteMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun deleteTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable assignmentTagId: UUID,
            @PathVariable userId: UUID) {
        ticketAssignmentService.deleteTicketAssignment(ticketId, assignmentTagId, userId)
    }

}