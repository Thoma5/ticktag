package io.ticktag.restinterface.ticketassignment.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticketassignment.schema.TicketAssignmentResultJson
import io.ticktag.service.Principal
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/ticket")
@Api(tags = arrayOf("ticketassignment"), description = "Ticket Assignment")
open class TicketAssignmentController @Inject constructor(
        private val ticketAssignmentService: TicketAssignmentService
) {
    @GetMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun getTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable assignmentTagId: UUID,
            @PathVariable userId: UUID): TicketAssignmentResultJson {
        val ticketAssignment = ticketAssignmentService.getTicketAssignment(ticketId, assignmentTagId, userId)
        return TicketAssignmentResultJson(ticketAssignment)
    }

    @PostMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun createTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable assignmentTagId: UUID,
            @PathVariable userId: UUID,
            @AuthenticationPrincipal principal: Principal): TicketAssignmentResultJson {
        val ticketAssignment = ticketAssignmentService.createTicketAssignment(ticketId, assignmentTagId, userId, principal)
        return TicketAssignmentResultJson(ticketAssignment)
    }

    @DeleteMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun deleteTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable assignmentTagId: UUID,
            @PathVariable userId: UUID,
            @AuthenticationPrincipal principal: Principal) {
        ticketAssignmentService.deleteTicketAssignment(ticketId, assignmentTagId, userId, principal)
    }

}