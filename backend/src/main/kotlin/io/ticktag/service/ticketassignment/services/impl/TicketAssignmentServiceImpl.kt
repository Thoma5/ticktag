package io.ticktag.service.member.services

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.AssignedTicketUserKey
import io.ticktag.persistence.ticket.entity.TicketAssignmentRepository
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.NotFoundException
import io.ticktag.service.member.dto.TicketAssignmentResult
import io.ticktag.service.timecategory.TicketAssignmentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*
import javax.inject.Inject

@TicktagService
open class TicketAssignmentServiceImpl @Inject constructor(
        private val ticketAssignments: TicketAssignmentRepository,
        private val tickets: TicketRepository,
        private val users: UserRepository,
        private val assignmentTags: AssignmentTagRepository
) : TicketAssignmentService {
    override fun getTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val ticketAssignment = ticketAssignments.findOne(AssignedTicketUserKey.create(ticket, assignmentTag, user)) ?: throw NotFoundException()
        return TicketAssignmentResult(ticketAssignment)
    }

    override fun createTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID): TicketAssignmentResult {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val ticketAssignment = AssignedTicketUser.create(ticket, assignmentTag, user)
        ticketAssignments.insert(ticketAssignment)
        return TicketAssignmentResult(ticketAssignment)
    }

    override fun deleteTicketAssignment(ticketId: UUID, tagId: UUID, userId: UUID) {
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()
        val assignmentTag = assignmentTags.findOne(tagId) ?: throw NotFoundException()
        val user = users.findOne(userId) ?: throw NotFoundException()
        val ticketAssignmentToDelete = ticketAssignments.findOne(AssignedTicketUserKey.create(ticket, assignmentTag, user)) ?: throw NotFoundException()
        ticketAssignments.delete(ticketAssignmentToDelete)
    }

    override fun listTicketAssignments(pageable: Pageable): Page<TicketAssignmentResult> {
        var page = ticketAssignments.findAll(pageable)
        var content = page.content.map(::TicketAssignmentResult)
        return PageImpl(content, pageable, page.totalElements)
    }

    override fun listUsersTicketAssignments(userId: UUID, pageable: Pageable): Page<TicketAssignmentResult> {
        var page = ticketAssignments.findByUserId(userId, pageable)
        var content = page.content.map(::TicketAssignmentResult)
        return PageImpl(content, pageable, page.totalElements)
    }

}


