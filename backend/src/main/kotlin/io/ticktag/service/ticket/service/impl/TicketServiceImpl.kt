package io.ticktag.service.ticket.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.LoggedTime
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketTag
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.*
import io.ticktag.service.ticket.dto.CreateTicket
import io.ticktag.service.ticket.dto.TicketProgressResult
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.dto.UpdateTicket
import io.ticktag.service.ticket.service.TicketService
import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Duration
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
        return result.map { toResultDto(it) }
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTickets(@P("authProjectId") project: UUID, pageable: Pageable): Page<TicketResult> {
        val page = tickets.findByProjectId(project, pageable)
        val content = page.content.map { toResultDto(it) }
        return PageImpl(content, pageable, page.totalElements)
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicket(@P("authTicketId") id: UUID): TicketResult {
        return toResultDto(tickets.findOne(id) ?: throw NotFoundException())
    }

    private fun loggedTimeForTicket(ticket: Ticket): Duration {
        var duration = Duration.ZERO
        for (comment: Comment in ticket.comments) {
            for (loggedTime: LoggedTime in comment.loggedTimes) {
                duration += loggedTime.time
            }
        }
        return duration
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicketProgress(id: UUID): TicketProgressResult {
        val ticket = tickets.findOne(id) ?: throw NotFoundException()
        var totalEstimatedTime = Duration.ZERO
        var totalLoggedTime = Duration.ZERO
        var ticketDuration: Duration
        var ticketEstimatedTime: Duration

        ticketEstimatedTime = ticket.currentEstimatedTime ?: ticket.initialEstimatedTime ?: Duration.ZERO
        totalEstimatedTime += ticketEstimatedTime

        ticketDuration = loggedTimeForTicket(ticket)
        if (ticketDuration > ticketEstimatedTime) { // logged Time may not be greater than logged Time
            ticketDuration = ticketEstimatedTime
        }

        totalLoggedTime += ticketDuration

        for (subTicket: Ticket in ticket.subTickets) {
            ticketEstimatedTime = subTicket.currentEstimatedTime ?: subTicket.initialEstimatedTime ?: Duration.ZERO
            totalEstimatedTime += ticketEstimatedTime

            ticketDuration = loggedTimeForTicket(subTicket)
            if (ticketDuration > ticketEstimatedTime) { // logged Time may not be greater than logged Time
                ticketDuration = ticketEstimatedTime
            }
            totalLoggedTime += ticketDuration
        }

        return TicketProgressResult(totalLoggedTime, totalEstimatedTime)
    }

    @PreAuthorize(AuthExpr.USER) // Checked manually
    override fun getTickets(ids: Collection<UUID>, principal: Principal): Map<UUID, TicketResult> {
        val permittedIds = ids.filter {
            principal.hasProjectRoleForTicket(it, AuthExpr.ROLE_PROJECT_OBSERVER) || principal.hasRole(AuthExpr.ROLE_GLOBAL_OBSERVER)
        }
        if (permittedIds.isEmpty()) {
            return emptyMap()
        }
        return tickets.findByIds(permittedIds).map({ toResultDto(it) }).associateBy { it.id }
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
        newSubs.addAll(createTicket.subTickets.map({ sub ->
            sub.parentTicket = newTicket.id
            createTicket(sub, principal, projectId).id
        }))

        newSubs.addAll(createTicket.existingSubTicketIds)

        newSubs.forEach { subID ->
            val subTicket = tickets.findOne(subID) ?: throw NotFoundException()
            subTicket.parentTicket = newTicket
        }

        // Neither EM nor UPDATECASCADE can reload the ticket
        val ticketResult = toResultDto(newTicket)
                .copy(subTicketIds = newSubs, ticketAssignments = ticketAssignmentList)
        return ticketResult
    }

    private fun implies(p: Boolean, q: Boolean): Boolean {
        return !p || q
    }

    //TODO: Log Changes in History
    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun updateTicket(@Valid updateTicket: UpdateTicket, @P("authTicketId") ticketId: UUID, principal: Principal): TicketResult {

        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()


        if (updateTicket.title != null) {
            ticket.title = updateTicket.title
        }
        if (updateTicket.open != null) {
            ticket.open = updateTicket.open
        }
        if (updateTicket.storyPoints != null) {
            ticket.storyPoints = updateTicket.storyPoints
        }
        if (updateTicket.initialEstimatedTime != null) {
            ticket.initialEstimatedTime = updateTicket.initialEstimatedTime
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

        return toResultDto(ticket)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun deleteTicket(@P("authTicketId") id: UUID) {
        tickets.delete(tickets.findOne(id) ?: throw NotFoundException())
    }

    private fun toResultDto(t: Ticket): TicketResult {
        val realCommentIds = t.comments.filter { c -> c.describedTicket == null }.map(Comment::id)
        val referencingTicketIds = t.mentioningComments.map { it.ticket.id }
        val referencedTicketIds = t.comments.flatMap { it.mentionedTickets }.map(Ticket::id)

        return TicketResult(id = t.id,
                number = t.number,
                createTime = t.createTime,
                title = t.title,
                open = t.open,
                storyPoints = t.storyPoints,
                initialEstimatedTime = t.initialEstimatedTime,
                currentEstimatedTime = t.currentEstimatedTime,
                dueDate = t.dueDate,
                description = t.descriptionComment.text,
                projectId = t.project.id,
                ticketAssignments = t.assignedTicketUsers.map(::TicketAssignmentResult),
                subTicketIds = t.subTickets.map(Ticket::id),
                parentTicketId = t.parentTicket?.id,
                createdBy = t.createdBy.id,
                tagIds = t.tags.map(TicketTag::id),
                referencedTicketIds = referencedTicketIds,
                referencingTicketIds = referencingTicketIds,
                commentIds = realCommentIds)
    }
}