package io.ticktag.service.ticket.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.TicketEventRepository
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.dto.TicketFilter
import io.ticktag.persistence.ticket.entity.*
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.*
import io.ticktag.service.command.service.CommandService
import io.ticktag.service.ticket.dto.CreateTicket
import io.ticktag.service.ticket.dto.ProgressResult
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
        private val ticketAssignmentService: TicketAssignmentService,
        private val commandService: CommandService,
        private val ticketEvents: TicketEventRepository
) : TicketService {
    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTicketsFuzzy(@P("authProjectId") project: UUID, query: String, pageable: Pageable): List<TicketResult> {
        val result = tickets.findByProjectIdAndFuzzy(project, "%$query%", "%$query%", pageable)
        return result.map { toResultDto(it) }
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTickets(@P("authProjectId") project: UUID,
                             number: Int?,
                             title: String?,
                             tags: List<String>?,
                             users: List<String>?,
                             progressOne: Float?,
                             progressTwo: Float?,
                             progressGreater: Boolean?,
                             dueDateOne: Instant?,
                             dueDateTwo: Instant?,
                             dueDateGreater: Boolean?,
                             storyPointsOne: Int?,
                             storyPointsTwo: Int?,
                             storyPointsGreater: Boolean?,
                             open: Boolean?,
                             pageable: Pageable): Page<TicketResult> {

        if ( progressOne?.isNaN()?:false || progressOne?.isInfinite()?:false ) {
            throw TicktagValidationException(listOf(ValidationError("listTickets", ValidationErrorDetail.Other("invalidValueProgressOne"))))
        }
        if ( progressTwo?.isNaN()?:false || progressTwo?.isInfinite()?:false ) {
            throw TicktagValidationException(listOf(ValidationError("listTickets", ValidationErrorDetail.Other("invalidValueProgressTwo"))))
        }
        if ( tags?.contains("")?:false ) {
            throw TicktagValidationException(listOf(ValidationError("listTickets", ValidationErrorDetail.Other("invalidValueInTags"))))
        }
        if ( users?.contains("")?:false ) {
            throw TicktagValidationException(listOf(ValidationError("listTickets", ValidationErrorDetail.Other("invalidValueInTags"))))
        }
        val filter = TicketFilter(project, number, title, tags, users, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater,  storyPointsOne, storyPointsTwo, storyPointsGreater, open)
        val page = tickets.findAll(filter, pageable)
        val content = page.content.map { toResultDto(it) }
        return PageImpl(content, pageable, page.totalElements)
    }
    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTickets(@P("authProjectId") project: UUID, pageable: Pageable): Page<TicketResult> {
        return listTickets(project, null, null, null, null, null, null, null, null, null, null, null, null, null, null, pageable)
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicket(@P("authTicketId") id: UUID): TicketResult {
        return toResultDto(tickets.findOne(id) ?: throw NotFoundException())
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
        val wantToSetSubTickets = (createTicket.subTickets.isNotEmpty()) || //creates New SubTickets
                (createTicket.existingSubTicketIds.isNotEmpty()) // references SubTickets

        if (wantToSetParentTicket && wantToSetSubTickets) {
            throw TicktagValidationException(listOf(ValidationError("createTicket", ValidationErrorDetail.Other("nonestedsubtickets"))))
        }

        val number = (tickets.findHighestTicketNumberInProject(createTicket.projectID) ?: 0) + 1
        val createTime = Instant.now()
        val title = createTicket.title
        val open: Boolean = createTicket.open
        val storyPoints = createTicket.storyPoints
        val initialEstimatedTime = createTicket.initialEstimatedTime
        val currentEstimatedTime = createTicket.currentEstimatedTime
        val dueDate = createTicket.dueDate
        val project = projects.findOne(createTicket.projectID) ?: throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()


        val parentTicket: Ticket? = createTicket.parentTicket?.let { tickets.findOne(it) }
        if (parentTicket?.parentTicket != null) {
            throw TicktagValidationException(listOf(ValidationError("createTicket", ValidationErrorDetail.Other("nonestedsubtickets"))))
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
            val ticketAssignmentResult = ticketAssignmentService.createTicketAssignment(newTicket.id, assignmentTagId, userId, principal)
            ticketAssignmentList.add(ticketAssignmentResult)
        }

        //SubTickets
        val newSubs: MutableList<UUID> = emptyList<UUID>().toMutableList()
        newSubs.addAll(createTicket.subTickets.map({ sub ->
            val subCreateReq = sub.copy(parentTicket = newTicket.id)
            createTicket(subCreateReq, principal, projectId).id
        }))
        newSubs.addAll(createTicket.existingSubTicketIds)

        // Execute commands
        commandService.applyCommands(newComment, createTicket.commands, principal)

        // Neither EM nor UPDATECASCADE can reload the ticket
        val ticketResult = toResultDto(newTicket)
                .copy(subTicketIds = newSubs, ticketAssignments = ticketAssignmentList)
        return ticketResult
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun updateTicket(@Valid updateTicket: UpdateTicket, @P("authTicketId") ticketId: UUID, principal: Principal): TicketResult {
        val user = users.findOne(principal.id) ?: throw NotFoundException()
        val ticket = tickets.findOne(ticketId) ?: throw NotFoundException()


        if (updateTicket.title != null) {
            if (ticket.title != updateTicket.title)
                ticketEvents.insert(TicketEventTitleChanged.create(ticket, user, ticket.title, updateTicket.title))
            ticket.title = updateTicket.title
        }
        if (updateTicket.open != null) {
            if (ticket.open != updateTicket.open)
                ticketEvents.insert(TicketEventStateChanged.create(ticket, user, ticket.open, updateTicket.open))
            ticket.open = updateTicket.open
        }
        if (updateTicket.storyPoints != null) {
            if (ticket.storyPoints != updateTicket.storyPoints)
                ticketEvents.insert(TicketEventStoryPointsChanged.create(ticket, user, ticket.storyPoints, updateTicket.storyPoints))
            ticket.storyPoints = updateTicket.storyPoints
        }
        if (updateTicket.initialEstimatedTime != null) {
            if (ticket.initialEstimatedTime != updateTicket.initialEstimatedTime)
                ticketEvents.insert(TicketEventInitialEstimatedTimeChanged.create(ticket, user, ticket.initialEstimatedTime, updateTicket.initialEstimatedTime))
            ticket.initialEstimatedTime = updateTicket.initialEstimatedTime
        }
        if (updateTicket.currentEstimatedTime != null) {
            if (ticket.currentEstimatedTime != updateTicket.currentEstimatedTime)
                ticketEvents.insert(TicketEventCurrentEstimatedTimeChanged.create(ticket, user, ticket.currentEstimatedTime, updateTicket.currentEstimatedTime))
            ticket.currentEstimatedTime = updateTicket.currentEstimatedTime
        }
        if (updateTicket.dueDate != null) {
            if (ticket.dueDate != updateTicket.dueDate)
                ticketEvents.insert(TicketEventDueDateChanged.create(ticket, user, ticket.dueDate, updateTicket.dueDate))
            ticket.dueDate = updateTicket.dueDate
        }

        if (updateTicket.parentTicket != null) {
            val parentTicket = tickets.findOne(updateTicket.parentTicket) ?: throw NotFoundException()

            if (parentTicket.parentTicket != null) {
                throw TicktagValidationException(listOf(ValidationError("updateTicket", ValidationErrorDetail.Other("nonestedsubtickets"))))
            }
            if (ticket.subTickets.isNotEmpty()) {
                throw TicktagValidationException(listOf(ValidationError("updateTicket", ValidationErrorDetail.Other("nonestedsubtickets"))))
            }

            if (ticket.parentTicket != parentTicket)
                ticketEvents.insert(TicketEventParentChanged.create(ticket, user, ticket.parentTicket, parentTicket))
            ticket.parentTicket = parentTicket
        }
        //Comment
        if (updateTicket.description != null) {
            if (ticket.descriptionComment.text != updateTicket.description)
                ticketEvents.insert(TicketEventCommentTextChanged.create(ticket, user, ticket.descriptionComment, ticket.descriptionComment.text, updateTicket.description))
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
                progress = ProgressResult(t.progress),
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