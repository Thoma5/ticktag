package io.ticktag.service.ticket.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
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

@TicktagService
open class TicketServiceImpl @Inject constructor(
        private val tickets: TicketRepository,
        private val projects: ProjectRepository,
        private val users: UserRepository,
        private val comments:CommentRepository,
        private val em: EntityManager

) :TicketService{

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTickets(@P("authProjectId") projectId: UUID): List<TicketResult> {
        return tickets.findByProjectId(projectId).map(::TicketResult)
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicket( @P("authTicketId")id:UUID): TicketResult{
        return TicketResult(tickets.findOne(id) ?: throw NotFoundException())
    }


    //TODO: Reference: Mentions, Tags, Assignee
    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun createTicket(createTicket:CreateTicket, principal: Principal, @P("authProjectId")projectId: UUID):TicketResult {
        val number = tickets.findNewTicketNumber(createTicket.projectID)?:1
        val createTime = Instant.now()
        val title = createTicket.title
        val open:Boolean = createTicket.open
        val storyPoints =createTicket.storyPoints
        val initialEstimatedTime = createTicket.initialEstimatedTime
        val currentEstimatedTime = createTicket.currentEstimatedTime
        val dueDate = createTicket.dueDate
        val project = projects.findOne(createTicket.projectID)?:throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()


        var parentTicket: Ticket? = null
        if (createTicket.partenTicket != null){
            parentTicket = tickets.findOne(createTicket.partenTicket)
        }
        var newTicket = Ticket.create(number,createTime,title,open,storyPoints,initialEstimatedTime,currentEstimatedTime,dueDate,parentTicket,project,user)
        tickets.insert(newTicket)

        //Comment
        val text = createTicket.description
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime, text, user, newTicket)
        comments.insert(newComment)
        newTicket.descriptionComment= newComment

        //SubTickets
        var newSubs = createTicket.subTickets.map ({ sub -> createTicket(sub,principal,projectId).id }).toMutableList()
        newSubs.addAll(createTicket.existingSubTicketIds)
        newSubs.forEach { subID ->
            val subTicket = tickets.findOne(subID) ?: throw NotFoundException()
            subTicket.parentTicket = newTicket
        }

        //newTicket = tickets.findOne(newTicket.id)?:throw NotFoundException()
        var ticketResult = TicketResult(newTicket) //Weder EM noch via UPDATECASCADE kann das ticket neu geladen werden
        ticketResult.subTicketIds = newSubs
        return ticketResult
    }

    //TODO: Log Changes in History
    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun updateTicket(updateTicket: UpdateTicket,@P("authTicketId")ticketId: UUID,principal: Principal): TicketResult{

        val ticket = tickets.findOne(ticketId)?:throw NotFoundException()
        if (updateTicket.title != null){
            ticket.title = updateTicket.title
        }
        if (updateTicket.open != null){
            ticket.open = updateTicket.open
        }
        if (updateTicket.storyPoints != null){
            ticket.storyPoints = updateTicket.storyPoints
        }
        if (updateTicket.currentEstimatedTime != null){
            ticket.currentEstimatedTime = updateTicket.currentEstimatedTime
        }
        if (updateTicket.dueDate != null){
            ticket.dueDate = updateTicket.dueDate
        }
        var parentTicket: Ticket? = null
        if (updateTicket.partenTicket != null){
            ticket.parentTicket = tickets.findOne(updateTicket.partenTicket)
        }

        //Comment
        if (updateTicket.description != null){
            ticket.descriptionComment.text = updateTicket.description
        }

        //SubTickets
        if (updateTicket.subTickets != null || updateTicket.existingSubTicketIds != null){
            ticket.subTickets.forEach { t -> t.parentTicket = null }
        }
        if (updateTicket.subTickets != null) {

            var newSubs = updateTicket.subTickets.map({ sub -> createTicket(sub, principal,ticket.project.id).id }).toMutableList()

            newSubs.forEach { subID ->
                val subTicket = tickets.findOne(subID) ?: throw NotFoundException()
                subTicket.parentTicket = ticket
            }
        }

        if (updateTicket.existingSubTicketIds != null) {
            var newSubs = updateTicket.existingSubTicketIds

            newSubs.forEach { subID ->
                val subTicket = tickets.findOne(subID) ?: throw NotFoundException()
                subTicket.parentTicket = ticket

            }
        }

        return TicketResult(ticket)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun deleteTicket(@P("authTicketId") id: UUID) {
        tickets.delete(tickets.findOne(id) ?: throw NotFoundException())
    }
}