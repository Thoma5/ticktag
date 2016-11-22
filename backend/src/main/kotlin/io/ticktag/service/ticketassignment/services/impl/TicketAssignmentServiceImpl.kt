package io.ticktag.service.member.services

import io.ticktag.TicktagService
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.AssignedTicketUserKey
import io.ticktag.persistence.ticket.entity.TicketAssignmentRepository
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.member.dto.TicketAssignmentResult
import io.ticktag.service.timecategory.TicketAssignmentService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class TicketAssignmentServiceImpl @Inject constructor(
        private val ticketAssignments: TicketAssignmentRepository,
        private val members: MemberRepository,
        private val tickets: TicketRepository,
        private val users: UserRepository,
        private val assignmentTags: AssignmentTagRepository
) : TicketAssignmentService {
    @PreAuthorize(AuthExpr.READ_TICKET_ASSIGNMENT)
    override fun getTicketAssignment(@P("authTicketId") ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val ticketAssignment = ticketAssignments.findOne(AssignedTicketUserKey.create(ticket, assignmentTag, user)) ?: throw NotFoundException()
        return TicketAssignmentResult(ticketAssignment)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_ASSIGNMENT)
    override fun createTicketAssignment(@P("authTicketId") ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        if (!assignmentTags.findByProjectId(ticket.project.id).contains(assignmentTag)) throw NotFoundException() //TODO: ExceptionMessage?
        val user = users.findOne(userId) ?: throw NotFoundException()
        members.findByUserIdAndProjectId(userId, ticket.project.id) ?: throw NotFoundException()
        val ticketAssignment = AssignedTicketUser.create(ticket, assignmentTag, user)
        ticketAssignments.insert(ticketAssignment)
        return TicketAssignmentResult(ticketAssignment)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_ASSIGNMENT)
    override fun deleteTicketAssignment(@P("authTicketId") ticketId: UUID, tagId: UUID, userId: UUID) {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val ticketAssignmentToDelete = ticketAssignments.findOne(AssignedTicketUserKey.create(ticket, assignmentTag, user)) ?: throw NotFoundException()
        ticketAssignments.delete(ticketAssignmentToDelete)
    }
}


