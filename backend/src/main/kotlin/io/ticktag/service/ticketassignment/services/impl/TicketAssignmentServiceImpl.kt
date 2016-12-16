package io.ticktag.service.ticketassignment.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.TicketEventRepository
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.AssignedTicketUserKey
import io.ticktag.persistence.ticket.entity.TicketEventUserAdded
import io.ticktag.persistence.ticket.entity.TicketEventUserRemoved
import io.ticktag.persistence.ticketassignment.TicketAssignmentRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.*
import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
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
        private val assignmentTags: AssignmentTagRepository,
        private val ticketEvents: TicketEventRepository
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
    override fun createTicketAssignment(@P("authTicketId") ticketId: UUID, tagId: UUID, userId: UUID, principal: Principal): TicketAssignmentResult {
        return createOrGetHelper(ticketId, tagId, userId, false, principal)

    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_ASSIGNMENT)
    override fun createOrGetIfExistsTicketAssignment(@P("authTicketId") ticketId: UUID, tagId: UUID, userId: UUID, principal: Principal): TicketAssignmentResult {
        return createOrGetHelper(ticketId, tagId, userId, true, principal)

    }

    private fun createOrGetHelper(ticketId: UUID, tagId: UUID, userId: UUID, receiveIfExists: Boolean, principal: Principal): TicketAssignmentResult {
        val userSelf = users.findOne(principal.id) ?: throw NotFoundException()
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        if (!assignmentTags.findByProjectId(ticket.project.id).contains(assignmentTag)) throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val member = members.findByUserIdAndProjectId(userId, ticket.project.id) ?: throw NotFoundException()
        if (member.role == ProjectRole.OBSERVER) {
            throw TicktagValidationException(listOf(ValidationError("member.role", ValidationErrorDetail.Other("notpermitted"))))
        }
        var ticketAssignment: AssignedTicketUser?
        ticketAssignment = null
        if (receiveIfExists) {
            ticketAssignment = ticketAssignments.findOne(AssignedTicketUserKey.create(ticket, assignmentTag, user))
        }
        if (ticketAssignment != null) {
            return TicketAssignmentResult(ticketAssignment)
        }
        ticketAssignment = AssignedTicketUser.create(ticket, assignmentTag, user)
        ticketAssignments.insert(ticketAssignment)
        ticketEvents.insert(TicketEventUserAdded.create(ticket, userSelf, user, assignmentTag))
        return TicketAssignmentResult(ticketAssignment)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_ASSIGNMENT)
    override fun deleteTicketAssignment(@P("authTicketId") ticketId: UUID, tagId: UUID, userId: UUID, principal: Principal) {
        val userSelf = users.findOne(principal.id) ?: throw NotFoundException()
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val ticketAssignmentToDelete = ticketAssignments.findOne(AssignedTicketUserKey.create(ticket, assignmentTag, user)) ?: throw NotFoundException()
        ticketAssignments.delete(ticketAssignmentToDelete)
        ticketEvents.insert(TicketEventUserRemoved.create(ticket, userSelf, user, assignmentTag))
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET_ASSIGNMENT)
    override fun deleteTicketAssignments(@P("authTicketId") ticketId: UUID, userId: UUID, principal: Principal) {
        val userSelf = users.findOne(principal.id) ?: throw NotFoundException()
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val ticketAssignmentsToDelete = ticketAssignments.findByUserIdAndTicketId(userId, ticketId)
        for (ticketAssignmentToDelete in ticketAssignmentsToDelete) {
            ticketEvents.insert(TicketEventUserRemoved.create(ticket, userSelf, user, ticketAssignmentToDelete.tag))
        }
        ticketAssignments.deleteByUserIdAndTicketId(userId, ticketId)
    }
}


