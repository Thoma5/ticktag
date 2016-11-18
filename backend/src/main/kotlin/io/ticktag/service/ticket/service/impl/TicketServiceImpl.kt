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
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject

@TicktagService
open class TicketServiceImpl @Inject constructor(
        private val tickets: TicketRepository,
        private val projects: ProjectRepository,
        private val users: UserRepository,
        private val comments:CommentRepository

) :TicketService{

    @PreAuthorize(AuthExpr.USER)
    override fun listTickets(projectId: UUID): List<TicketResult> {
        return tickets.findByProjectId(projectId).map(::TicketResult)
    }

    @PreAuthorize(AuthExpr.USER)
    override fun getTicket(id:UUID): TicketResult{
        return TicketResult(tickets.findOne(id) ?: throw NotFoundException())
    }


    //TODO: Reference: Mentions, Tags, Assignee
    @PreAuthorize(AuthExpr.USER)
    override fun createTicket(createTicket:CreateTicket, principal: Principal):TicketResult {
        val number = createTicket.number
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
        var newSubs = createTicket.subTickets.map ({ sub -> createTicket(sub,principal).id }).toMutableList()
        newSubs.addAll(createTicket.existingSubTicketIds)
        newSubs.forEach { subID ->
            val subTicket = tickets.findOne(subID) ?: throw NotFoundException()
            subTicket.parentTicket = newTicket
        }

        newTicket = tickets.findOne(newTicket.id)?:throw NotFoundException()

        return TicketResult(newTicket)
    }

    //TODO: Log Changes in History
    @PreAuthorize(AuthExpr.USER)
    override fun updateTicket(updateTicket: UpdateTicket,ticketId: UUID,principal: Principal): TicketResult{

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

            var newSubs = updateTicket.subTickets.map({ sub -> createTicket(sub, principal).id }).toMutableList()

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
}