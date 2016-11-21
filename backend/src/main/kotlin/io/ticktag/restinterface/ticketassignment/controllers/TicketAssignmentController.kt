package io.ticktag.restinterface.ticketassignment.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.service.member.dto.TicketAssignmentResultJson
import io.ticktag.service.timecategory.TicketAssignmentService
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
            @PathVariable tagId: UUID,
            @PathVariable userId: UUID): TicketAssignmentResultJson {
        val ticketAssignment = ticketAssignmentService.getTicketAssignment(ticketId, tagId, userId)
        return TicketAssignmentResultJson(ticketAssignment)
    }

    @PostMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun createTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable tagId: UUID,
            @PathVariable userId: UUID): TicketAssignmentResultJson {
        val ticketAssignment = ticketAssignmentService.createTicketAssignment(ticketId, tagId, userId)
        return TicketAssignmentResultJson(ticketAssignment)
    }

    @DeleteMapping(value = "{ticketId}/tag/{assignmentTagId}/user/{userId}")
    open fun deleteTicketAssignment(
            @PathVariable ticketId: UUID,
            @PathVariable tagId: UUID,
            @PathVariable userId: UUID) {
        ticketAssignmentService.deleteTicketAssignment(ticketId, tagId, userId)
    }

}