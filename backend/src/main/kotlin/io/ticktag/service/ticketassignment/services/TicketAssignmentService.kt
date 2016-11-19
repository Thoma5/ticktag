package io.ticktag.service.timecategory

import io.ticktag.service.member.dto.TicketAssignmentResult
import java.util.*

interface TicketAssignmentService {
    fun getTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult
    fun createTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult
    fun deleteTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID)
}