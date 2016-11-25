package io.ticktag.service.ticket.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.*
import io.ticktag.service.ticket.dto.CreateTicket
import io.ticktag.service.ticket.dto.TicketAssignment
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.dto.UpdateTicket
import io.ticktag.service.ticket.service.TicketService
import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import org.springframework.data.domain.Pageable
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class TicketServiceImpl @Inject constructor(
        private val tickets: TicketRepository,
        private val projects: ProjectRepository,
        private val users: UserRepository,
        private val comments: CommentRepository,
        private val ticketAssignmentService: TicketAssignmentService

) : TicketService {
    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTicketsFuzzy(@P("authProjectId") project: UUID, query: String, pageable: Pageable): List<TicketResult> {
        val result = tickets.findByProjectIdAndFuzzy(project, "%$query%", "%$query%", pageable)
        return result.map(::TicketResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)

    override fun listTickets(@P("authProjectId") project: UUID, pageable: Pageable): List<TicketResult> {
        return tickets.findByProjectId(project, pageable).map(::TicketResult)
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicket(@P("authTicketId") id: UUID): TicketResult {
        return TicketResult(tickets.findOne(id) ?: throw NotFoundException())
    }


    //TODO: Reference: Mentions, Tags
    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun createTicket(@Valid createTicket: CreateTicket, principal: Principal, @P("authProjectId") projectId: UUID): TicketResult {

        val wantToSetParentTicket = createTicket.parentTicket != null
        val dontWantToCreateSubTicketsInThisUpdate = createTicket.subTickets.isEmpty()
        val dontWantToReferenceSubTicketsInThisUpdate = createTicket.existingSubTicketIds.isEmpty()

        //implies(q,p) is only false if q is true and p is false
        if (!(implies(wantToSetParentTicket, (dontWantToCreateSubTicketsInThisUpdate && dontWantToReferenceSubTicketsInThisUpdate)))) {
            throw TicktagValidationException(listOf(ValidationError("updateUser.parentTicket", ValidationErrorDetail.Other("subTickets are Set"))))
        }

        val wantToSetSubTickets = (createTicket.subTickets.isNotEmpty()) || //creates New SubTickets
                (createTicket.existingSubTicketIds.isNotEmpty()) // references SubTickets
        val dontWantToSetParentTicket = createTicket.parentTicket == null

        if (!(implies(wantToSetSubTickets, dontWantToSetParentTicket))) {
            throw TicktagValidationException(listOf(ValidationError("updateUser.subTickets", ValidationErrorDetail.Other("tickets are Set"))))
        }


        val number = tickets.findNewTicketNumber(createTicket.projectID) ?: 1
        val createTime = Instant.now()
        val title = createTicket.title
        val open: Boolean = createTicket.open
        val storyPoints = createTicket.storyPoints
        val initialEstimatedTime = createTicket.initialEstimatedTime
        val currentEstimatedTime = createTicket.currentEstimatedTime
        val dueDate = createTicket.dueDate
        val project = projects.findOne(createTicket.projectID) ?: throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()


        var parentTicket: Ticket? = null
        val parentTicketCopy = createTicket.parentTicket
        if ((parentTicketCopy != null)) {
            parentTicket = tickets.findOne(parentTicketCopy)
        }
        val newTicket = Ticket.create(number, createTime, title, open, storyPoints, initialEstimatedTime, currentEstimatedTime, dueDate, parentTicket, project, user)
        tickets.insert(newTicket)

        //Comment
        val text = createTicket.description
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime, text, user, newTicket)
        comments.insert(newComment)
        newTicket.descriptionComment = newComment

        //Assignee
        val ticketAssignmentList = emptyList<TicketAssignmentResult>().toMutableList() // Attach this list after the conversion of newTicket to ticketResult to avoid code duplication
        for ((assignmentTagId, userId) in createTicket.ticketAssignments) {
            val ticketAssignmentResult = ticketAssignmentService.createTicketAssignment(newTicket.id, assignmentTagId, userId)
            ticketAssignmentList.add(ticketAssignmentResult)
        }

        //SubTickets
        val newSubs: MutableList<UUID> = emptyList<UUID>().toMutableList()
        if (createTicket.subTickets != null) {
            newSubs.addAll(createTicket.subTickets.map({ sub ->
                sub.parentTicket = newTicket.id
                createTicket(sub, principal, projectId).id
            }))
        }

        if (createTicket.existingSubTicketIds != null) {
            newSubs.addAll(createTicket.existingSubTicketIds)
        }

        newSubs.forEach { subID ->
            val subTicket = tickets.findOne(subID) ?: throw NotFoundException()
            subTicket.parentTicket = newTicket
        }

        val ticketResult = TicketResult(newTicket) //Weder EM noch via UPDATECASCADE kann das ticket neu geladen werden
        ticketResult.subTicketIds = newSubs
        ticketResult.ticketAssignments = ticketAssignmentList
        return ticketResult
    }

    private fun implies(p: Boolean, q: Boolean): Boolean {
        return !p || q
    }

    //TODO: Log Changes in History
    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun updateTicket(@Valid updateTicket: UpdateTicket, @P("authTicketId") ticketId: UUID, principal: Principal): TicketResult {

        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()

        val wantToSetParentTicket = updateTicket.parentTicket != null
        val dontWantToCreateSubTicketsInThisUpdate = updateTicket.subTickets == null || updateTicket.subTickets.isEmpty()
        val dontWantToReferenceSubTicketsInThisUpdate = updateTicket.existingSubTicketIds == null || (updateTicket.existingSubTicketIds.isEmpty())

        val noSubTicketsArePresentBeforeUpdate = (ticket.subTickets.size == 0)

        //implies(q,p) is only false if q is true and p is false
        if (!(implies(wantToSetParentTicket, (noSubTicketsArePresentBeforeUpdate || (dontWantToCreateSubTicketsInThisUpdate && dontWantToReferenceSubTicketsInThisUpdate))))) {
            throw TicktagValidationException(listOf(ValidationError("updateUser.parentTicket", ValidationErrorDetail.Other("subTickets are Set"))))
        }

        val wantToSetSubTickets = (updateTicket.subTickets != null && updateTicket.subTickets.isNotEmpty()) || //creates New SubTickets
                (updateTicket.existingSubTicketIds != null && updateTicket.existingSubTicketIds.isNotEmpty())  // references SubTickets
        val dontWantToSetParentTicket = updateTicket.parentTicket == null
        val noParentTicketIsPresentBeforeUpdate = ticket.parentTicket == null

        if (!(implies(wantToSetSubTickets, (dontWantToSetParentTicket || noParentTicketIsPresentBeforeUpdate)))) {
            throw TicktagValidationException(listOf(ValidationError("updateUser.subTickets", ValidationErrorDetail.Other("tickets are Set"))))
        }


        if (updateTicket.title != null) {
            ticket.title = updateTicket.title
        }
        if (updateTicket.open != null) {
            ticket.open = updateTicket.open
        }
        if (updateTicket.storyPoints != null) {
            ticket.storyPoints = updateTicket.storyPoints
        }
        if (updateTicket.currentEstimatedTime != null) {
            ticket.currentEstimatedTime = updateTicket.currentEstimatedTime
        }
        if (updateTicket.dueDate != null) {
            ticket.dueDate = updateTicket.dueDate
        }

        if (updateTicket.parentTicket != null) {
            ticket.parentTicket = tickets.findOne(updateTicket.parentTicket)
        }

        //Comment
        if (updateTicket.description != null) {
            ticket.descriptionComment.text = updateTicket.description
        }
        val ticketResult = TicketResult(ticket)

        //Assignee
        if (updateTicket.ticketAssignments != null) {
            val ticketAssignmentList = emptyList<TicketAssignmentResult>().toMutableList()
            for ((assignmentTagId, userId) in updateTicket.ticketAssignments) {
                ticketAssignmentList.add(ticketAssignmentService.createOrGetIfExistsTicketAssignment(ticket.id, assignmentTagId, userId))
            }
            val ticketAssignmentDtos = ticket.assignedTicketUsers.map(::TicketAssignment)
            for ((assignmentTagId, userId) in ticketAssignmentDtos) {
                if (!ticketAssignmentList.contains(TicketAssignmentResult(ticket.id, assignmentTagId, userId))) {
                    ticketAssignmentService.deleteTicketAssignment(ticket.id, assignmentTagId, userId)
                }
            }
            ticketResult.ticketAssignments = ticketAssignmentList
        }

        //SubTickets
        if (updateTicket.subTickets != null || updateTicket.existingSubTicketIds != null) {
            ticket.subTickets.forEach { t -> t.parentTicket = null }
            val newSubs: MutableList<UUID> = emptyList<UUID>().toMutableList()
            if (updateTicket.subTickets != null) {
                newSubs.addAll(updateTicket.subTickets.map({ sub ->
                    sub.parentTicket = ticket.id
                    createTicket(sub, principal, ticket.project.id).id
                }))
            }

            if (updateTicket.existingSubTicketIds != null) {
                newSubs.addAll(updateTicket.existingSubTicketIds)
            }

            newSubs.forEach { subID ->
                val subTicket = tickets.findOne(subID) ?: throw NotFoundException()
                subTicket.parentTicket = ticket
            }
            ticketResult.subTicketIds = newSubs
        }

        return ticketResult
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun deleteTicket(@P("authTicketId") id: UUID) {
        tickets.delete(tickets.findOne(id) ?: throw NotFoundException())
    }
}