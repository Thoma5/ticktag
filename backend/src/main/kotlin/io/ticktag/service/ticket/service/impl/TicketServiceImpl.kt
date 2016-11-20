package io.ticktag.service.ticket.service.impl

import com.sun.org.apache.xml.internal.utils.MutableAttrListImpl
import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.*
import io.ticktag.service.project.dto.CreateTicket
import io.ticktag.service.project.dto.UpdateTicket
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.service.TicketService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.validation.Valid

@TicktagService
open class TicketServiceImpl @Inject constructor(
        private val tickets: TicketRepository,
        private val projects: ProjectRepository,
        private val users: UserRepository,
        private val comments: CommentRepository,
        private val em: EntityManager

) : TicketService {

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTickets(@P("authProjectId") projectId: UUID): List<TicketResult> {
        return tickets.findByProjectId(projectId).map(::TicketResult)
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicket(@P("authTicketId") id: UUID): TicketResult {
        return TicketResult(tickets.findOne(id) ?: throw NotFoundException())
    }


    //TODO: Reference: Mentions, Tags, Assignee
    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun createTicket(@Valid createTicket: CreateTicket, principal: Principal, @P("authProjectId") projectId: UUID): TicketResult {

        if (!(!(createTicket.partenTicket != null) || (createTicket.subTickets != null && createTicket.subTickets.size == 0 &&
                createTicket.existingSubTicketIds != null && createTicket.existingSubTicketIds.size == 0))) {
            throw TicktagValidationException(listOf(ValidationError("updateUser.parentTicket", ValidationErrorDetail.Other("subTickets are Set"))))
        }
        //Implies == !p||q
        if (!(!(createTicket.subTickets != null && createTicket.subTickets.size != 0 || createTicket.existingSubTicketIds != null && createTicket.existingSubTicketIds.size != 0) ||
                (createTicket.partenTicket == null))) {
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
        var parentTicketCopy = createTicket.partenTicket
        if ((parentTicketCopy != null)) {
            parentTicket = tickets.findOne(parentTicketCopy)
        }
        var newTicket = Ticket.create(number, createTime, title, open, storyPoints, initialEstimatedTime, currentEstimatedTime, dueDate, parentTicket, project, user)
        tickets.insert(newTicket)

        //Comment
        val text = createTicket.description
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime, text, user, newTicket)
        comments.insert(newComment)
        newTicket.descriptionComment = newComment

        //SubTickets
        val newSubs: MutableList<UUID> = emptyList<UUID>().toMutableList()
        if (createTicket.subTickets != null) {
            newSubs.addAll(createTicket.subTickets.map({ sub ->
                sub.partenTicket = newTicket.id
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

        var ticketResult = TicketResult(newTicket) //Weder EM noch via UPDATECASCADE kann das ticket neu geladen werden
        ticketResult.subTicketIds = newSubs
        return ticketResult
    }

    //TODO: Log Changes in History
    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun updateTicket(@Valid updateTicket: UpdateTicket, @P("authTicketId") ticketId: UUID, principal: Principal): TicketResult {

        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()

        if (!(!(updateTicket.partenTicket != null) ||
                (ticket.subTickets.size == 0 || (updateTicket.subTickets != null && updateTicket.subTickets.size == 0)))) {
            throw TicktagValidationException(listOf(ValidationError("updateUser.parentTicket", ValidationErrorDetail.Other("subTickets are Set"))))
        }

        if (!(!(updateTicket.subTickets != null && updateTicket.subTickets.size != 0) ||
                (ticket.parentTicket == null || updateTicket.partenTicket == null))) {
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

        if (updateTicket.partenTicket != null) {
            ticket.parentTicket = tickets.findOne(updateTicket.partenTicket)
        }

        //Comment
        if (updateTicket.description != null) {
            ticket.descriptionComment.text = updateTicket.description
        }
        var ticketResult = TicketResult(ticket)
        //SubTickets
        if (updateTicket.subTickets != null || updateTicket.existingSubTicketIds != null) {
            ticket.subTickets.forEach { t -> t.parentTicket = null }
            val newSubs: MutableList<UUID> = emptyList<UUID>().toMutableList()
            if (updateTicket.subTickets != null) {
                newSubs.addAll(updateTicket.subTickets.map({ sub ->
                    sub.partenTicket = ticket.id
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