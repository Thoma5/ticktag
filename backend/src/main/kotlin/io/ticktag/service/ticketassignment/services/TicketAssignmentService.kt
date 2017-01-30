package io.ticktag.service.ticketassignment.services

import io.ticktag.service.Principal
import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import java.util.*

interface TicketAssignmentService {
    /**
     * Get the an assigned user for a Tickets
     */
    fun getTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult

    /**
     * Assign a User to a ticket
     */
    fun createTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID, principal: Principal): TicketAssignmentResult

    /**
     * Similar to creatTicketAssignment but only creates ticket if it not already exits
     */
    fun createOrGetIfExistsTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID, principal: Principal): TicketAssignmentResult

    /**
     * Delete a User assignment to a Ticket
     */
    fun deleteTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID, principal: Principal)

    /**
     * Delete all Assignments of a User to a ticket
     */
    fun deleteTicketAssignments(ticketId: UUID, userId: UUID, principal: Principal)
}