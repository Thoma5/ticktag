package io.ticktag.service.timecategory

import io.ticktag.service.member.dto.TicketAssignmentResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface TicketAssignmentService {
    fun getTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult
    fun createTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult
    fun deleteTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID)
    fun listTicketAssignments(pageable: Pageable): Page<TicketAssignmentResult>
    fun listUsersTicketAssignments(userId: UUID, pageable: Pageable): Page<TicketAssignmentResult>
}