package io.ticktag.service.ticket.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.project.dto.CreateTicket
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
        private val projects: ProjectRepository

) :TicketService{

    @PreAuthorize(AuthExpr.USER)
    override fun listTickets(): List<TicketResult> {
        return tickets.findAll().map(::TicketResult)
    }

    @PreAuthorize(AuthExpr.USER)
    override fun getTicket(id:UUID): TicketResult{
        return TicketResult(tickets.findOne(id) ?: throw NotFoundException())
    }

    override fun createTicket(createTicket:CreateTicket):TicketResult {
        val id=createTicket.id
        val number = createTicket.number
        val createTime = createTicket.createTime
        val title = createTicket.title
        val open:Boolean = createTicket.open
        val storyPoints =createTicket.storyPoints
        val initialEstimatedTime = createTicket.initialEstimatedTime
        val currentEstimatedTime = createTicket.currentEstimatedTime
        val dueDate = createTicket.dueDate
        val project = projects.findOne(createTicket.id)?:throw NotFoundException()

        

        //create_by
        //description


        //SubTickets?
        //tags
        //MentioningComments
        //assignedUsers

        val description: String
    }
}