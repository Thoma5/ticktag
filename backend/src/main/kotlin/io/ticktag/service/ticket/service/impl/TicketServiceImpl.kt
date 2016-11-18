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

    @PreAuthorize(AuthExpr.USER)
    override fun createTicket(createTicket:CreateTicket, principal: Principal):TicketResult {
        val number = createTicket.number
        val createTime = createTicket.createTime
        val title = createTicket.title
        val open:Boolean = createTicket.open
        val storyPoints =createTicket.storyPoints
        val initialEstimatedTime = createTicket.initialEstimatedTime
        val currentEstimatedTime = createTicket.currentEstimatedTime
        val dueDate = createTicket.dueDate
        val project = projects.findOne(createTicket.projectID)?:throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()
        val create_by = user
        //SuperTicket
        //tags
        //MentioningComments
        //assignedUsers

        val newTicket = Ticket.create(number,createTime,title,open,storyPoints,initialEstimatedTime,currentEstimatedTime,dueDate,null,project,user)

        tickets.insert(newTicket)

        //Comment
        val text = createTicket.description
        val creationTime = Instant.now()
        val newComment = Comment.create(creationTime, text, user, newTicket)
        comments.insert(newComment)
        newTicket.descriptionComment= newComment

        return TicketResult(newTicket)
    }
}