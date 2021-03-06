package io.ticktag.service.ticket.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.ticket.TicketEventRepository
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.dto.TicketFilter
import io.ticktag.persistence.ticket.entity.*
import io.ticktag.persistence.ticketassignment.TicketAssignmentRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.persistence.user.entity.User
import io.ticktag.service.*
import io.ticktag.service.command.service.CommandService
import io.ticktag.service.ticket.dto.*
import io.ticktag.service.ticket.service.TicketService
import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import io.ticktag.service.ticketassignment.services.TicketAssignmentService
import org.slf4j.LoggerFactory
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
        private val assignments: TicketAssignmentRepository,
        private val ticketAssignmentService: TicketAssignmentService,
        private val ticketEvents: TicketEventRepository
) : TicketService {
    @Inject()
    private lateinit var commandService: CommandService
    companion object {
        private val LOG = LoggerFactory.getLogger(TicketServiceImpl::class.java)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun getTicket(@P("authProjectId") projectId: UUID, ticketNumber: Int): TicketResult {
        return toResultDto(tickets.findByProjectIdAndNumber(projectId, ticketNumber) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTicketsFuzzy(@P("authProjectId") project: UUID, query: String, pageable: Pageable): List<TicketResult> {
        val result = tickets.findByProjectIdAndFuzzy(project, query, query, pageable)
        return toResultDtos(result)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTicketsOverview(@P("authProjectId") project: UUID,
                                     numbers: List<Int>?,
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
                                     parent: Int?,
                                     pageable: Pageable): Page<TicketOverviewResult> {
        if (progressOne?.isNaN() ?: false || progressOne?.isInfinite() ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.ProgressOne", ValidationErrorDetail.Other("invalidValue"))))
        }
        if (progressTwo?.isNaN() ?: false || progressTwo?.isInfinite() ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.ProgressTwo", ValidationErrorDetail.Other("invalidValue"))))
        }
        if (tags?.contains("") ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.Tags", ValidationErrorDetail.Other("invalidValue"))))
        }
        if (users?.contains("") ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.Tags", ValidationErrorDetail.Other("invalidValue"))))
        }
        val filter = TicketFilter(project, if (numbers?.size == 0) null else numbers, title, tags, users, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo, storyPointsGreater, open, parent)
        val page = tickets.findAll(filter, pageable)
        val content = page.content.map(::TicketOverviewResult)
        return PageImpl(content, pageable, page.totalElements)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTicketsStoryPoints(@P("authProjectId") project: UUID,
                                        numbers: List<Int>?,
                                        title: String?,
                                        tags: List<String>?,
                                        users: List<String>?,
                                        progressOne: Float?,
                                        progressTwo: Float?,
                                        progressGreater: Boolean?,
                                        dueDateOne: Instant?, dueDateTwo: Instant?,
                                        dueDateGreater: Boolean?,
                                        storyPointsOne: Int?,
                                        storyPointsTwo: Int?,
                                        storyPointsGreater: Boolean?,
                                        open: Boolean?,
                                        parent: Int?): List<TicketStoryPointResult> {
        if (progressOne?.isNaN() ?: false || progressOne?.isInfinite() ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.ProgressOne", ValidationErrorDetail.Other("invalidValue"))))
        }
        if (progressTwo?.isNaN() ?: false || progressTwo?.isInfinite() ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.ProgressTwo", ValidationErrorDetail.Other("invalidValue"))))
        }
        if (tags?.contains("") ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.Tags", ValidationErrorDetail.Other("invalidValue"))))
        }
        if (users?.contains("") ?: false) {
            throw TicktagValidationException(listOf(ValidationError("listTickets.Users", ValidationErrorDetail.Other("invalidValue"))))
        }
        val filter = TicketFilter(project, if (numbers?.size == 0) null else numbers, title, tags, users, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo, storyPointsGreater, null, parent)
        val ticketResult = tickets.findAll(filter)
        return ticketResult.map { t -> TicketStoryPointResult(t.id, t.open, t.storyPoints) }
    }


    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listTickets(@P("authProjectId") project: UUID, pageable: Pageable): Page<TicketResult> {
        val page = tickets.findByProjectId(project, pageable)
        val content = toResultDtos(page.content)
        return PageImpl(content, pageable, page.totalElements)
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
        return toResultDtos(tickets.findByIds(permittedIds)).associateBy { it.id }
    }

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
        val initialEstimatedTime = createTicket.initialEstimatedTime ?: createTicket.currentEstimatedTime
        val currentEstimatedTime = createTicket.currentEstimatedTime ?: createTicket.initialEstimatedTime
        val dueDate = createTicket.dueDate
        val project = projects.findOne(createTicket.projectID) ?: throw NotFoundException()
        val user = users.findOne(principal.id) ?: throw NotFoundException()


        val parentTicket: Ticket? = createTicket.parentTicket?.let { tickets.findOne(it) }
        if (parentTicket?.parentTicket != null) {
            throw TicktagValidationException(listOf(ValidationError("createTicket", ValidationErrorDetail.Other("nonestedsubtickets"))))
        }
        val newTicket = Ticket.create(number, createTime, title, open, storyPoints, null, null, dueDate, parentTicket, project, user)
        setEstimations(newTicket, UpdateValue(initialEstimatedTime), UpdateValue(currentEstimatedTime))
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

        //Save open Date in Events
        ticketEvents.insert(TicketEventStateChanged.create(newTicket, user, false, true))

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
            if (ticket.title != updateTicket.title.value)
                ticketEvents.insert(TicketEventTitleChanged.create(ticket, user, ticket.title, updateTicket.title.value))
            ticket.title = updateTicket.title.value
        }
        if (updateTicket.open != null) {
            if (ticket.open != updateTicket.open.value)
                ticketEvents.insert(TicketEventStateChanged.create(ticket, user, ticket.open, updateTicket.open.value))
            ticket.open = updateTicket.open.value
        }
        if (updateTicket.storyPoints != null) {
            if (ticket.storyPoints != updateTicket.storyPoints.value)
                ticketEvents.insert(TicketEventStoryPointsChanged.create(ticket, user, ticket.storyPoints, updateTicket.storyPoints.value))
            ticket.storyPoints = updateTicket.storyPoints.value
        }

        setEstimationsWithEvents(ticket, updateTicket.initialEstimatedTime, updateTicket.currentEstimatedTime, user)

        if (updateTicket.dueDate != null) {
            if (ticket.dueDate != updateTicket.dueDate.value)
                ticketEvents.insert(TicketEventDueDateChanged.create(ticket, user, ticket.dueDate, updateTicket.dueDate.value))
            ticket.dueDate = updateTicket.dueDate.value
        }

        if (updateTicket.parentTicket != null) {
            val parentTicket = if (updateTicket.parentTicket.value != null) {
                val parentTicket = tickets.findOne(updateTicket.parentTicket.value) ?: throw NotFoundException()

                if (parentTicket.parentTicket != null) {
                    throw TicktagValidationException(listOf(ValidationError("updateTicket", ValidationErrorDetail.Other("nonestedsubtickets"))))
                }
                if (ticket.subTickets.isNotEmpty()) {
                    throw TicktagValidationException(listOf(ValidationError("updateTicket", ValidationErrorDetail.Other("nonestedsubtickets"))))
                }

                parentTicket
            } else {
                null
            }

            if (ticket.parentTicket != parentTicket)
                ticketEvents.insert(TicketEventParentChanged.create(ticket, user, ticket.parentTicket, parentTicket))
            ticket.parentTicket = parentTicket
        }

        //Close Parent Ticket if it has no estimated time and all sub tickets are closed
        if (!ticket.open && ticket.parentTicket != null) {
            val parentTicket = ticket.parentTicket ?: throw NotFoundException()
            if (parentTicket.open && parentTicket.currentEstimatedTime == null) {
                val subtickets = tickets.findSubticketsByTicketIds(listOf(parentTicket.id)).get(parentTicket.id).orEmpty()
                val closeParent = subtickets.none { it.id != ticketId && it.open }
                if (closeParent) {
                    var updateParentTicket = UpdateTicket(open = UpdateValue(false), commands = null, currentEstimatedTime = null, description = null, dueDate = null, initialEstimatedTime = null, parentTicket = null, storyPoints = null, title = null)
                    updateTicket(updateParentTicket, parentTicket.id, principal)
                }
            }
        }

        //Comment
        if (updateTicket.description != null) {
            if (ticket.descriptionComment.text != updateTicket.description.value)
                ticketEvents.insert(TicketEventCommentTextChanged.create(ticket, user, ticket.descriptionComment, ticket.descriptionComment.text, updateTicket.description.value))
            ticket.descriptionComment.text = updateTicket.description.value
        }
        if (updateTicket.commands != null) {
            commandService.applyCommands(ticket.descriptionComment, updateTicket.commands, principal)
        }

        return toResultDto(ticket)
    }

    @PreAuthorize(AuthExpr.WRITE_TICKET)
    override fun deleteTicket(@P("authTicketId") id: UUID) {
        tickets.delete(tickets.findOne(id) ?: throw NotFoundException())
    }

    private fun setEstimationsWithEvents(ticket: Ticket, updateInitial: UpdateValue<Duration?>?, updateCurrent: UpdateValue<Duration?>?, user: User) {
        val prevInitial = ticket.initialEstimatedTime
        val prevCurrent = ticket.currentEstimatedTime

        setEstimations(ticket, updateInitial, updateCurrent)

        if (ticket.initialEstimatedTime != prevInitial) {
            ticketEvents.insert(TicketEventInitialEstimatedTimeChanged.create(ticket, user, prevInitial, ticket.initialEstimatedTime))
        }
        if (ticket.currentEstimatedTime != prevCurrent) {
            ticketEvents.insert(TicketEventCurrentEstimatedTimeChanged.create(ticket, user, prevCurrent, ticket.currentEstimatedTime))
        }
    }

    private fun setEstimations(ticket: Ticket, updateInitial: UpdateValue<Duration?>?, updateCurrent: UpdateValue<Duration?>?) {
        val initial = if (updateInitial != null) {
            updateInitial.value
        } else {
            ticket.initialEstimatedTime
        }
        val current = if (updateCurrent != null) {
            updateCurrent.value
        } else {
            ticket.currentEstimatedTime
        }

        if (initial == null && current == null) {
            ticket.initialEstimatedTime = null
            ticket.currentEstimatedTime = null
        } else if (initial == null && current != null) {
            if (updateInitial == null && updateCurrent != null) {
                ticket.initialEstimatedTime = current
                ticket.currentEstimatedTime = current
            } else {
                ticket.initialEstimatedTime = null
                ticket.currentEstimatedTime = null
            }
        } else if (initial != null && current == null) {
            ticket.initialEstimatedTime = initial
            ticket.currentEstimatedTime = initial
        } else if (initial != null && current != null) {
            ticket.initialEstimatedTime = initial
            ticket.currentEstimatedTime = current
        }
    }

    private fun toResultDtos(ts: Collection<Ticket>): List<TicketResult> {
        LOG.trace("Getting ticket ids")
        val ids = ts.map(Ticket::id)
        LOG.trace("Getting comments")
        val realComments = comments.findNonDescriptionCommentsByTicketIds(ids)

        LOG.trace("Getting mentioned tickets")
        val mentionedTickets = tickets.findMentionedTickets(ids)
        LOG.trace("Getting mentioning tickets")
        val mentioningTickets = tickets.findMentioningTickets(ids)

        LOG.trace("Getting progress")
        val progresses = tickets.findProgressesByTicketIds(ids)

        LOG.trace("Getting subtickets")
        val subtickets = tickets.findSubticketsByTicketIds(ids)

        LOG.trace("Getting assignments")
        val assignedUsers = assignments.findByTicketIds(ids)

        LOG.trace("Getting descriptions")
        val descriptions = comments.findDescriptionCommentsByTicketIds(ids)

        LOG.trace("Getting parent tickets")
        val parentTickets = tickets.findParentTicketsByTicketIds(ids)

        LOG.trace("Getting creators")
        val creators = users.findCreatorsByTicketIds(ids)

        LOG.trace("Getting projects")
        val allProjects = projects.findByTicketIds(ids)

        LOG.trace("Getting tags")
        val tags = tickets.findTagsByTicketIds(ids)

        LOG.trace("Mapping")
        val dtos = ts.map {
            toResultDtoInternal(it,
                    realComments,
                    mentioningTickets,
                    mentionedTickets,
                    progresses,
                    subtickets,
                    assignedUsers,
                    descriptions,
                    parentTickets,
                    creators,
                    allProjects,
                    tags
            )
        }
        return dtos
    }

    private fun toResultDto(t: Ticket): TicketResult {
        return toResultDtos(listOf(t))[0]
    }

    private fun toResultDtoInternal(t: Ticket,
                                    allNormalComments: Map<UUID, List<Comment>>,
                                    allReferencingTickets: Map<UUID, List<Ticket>>,
                                    allReferencedTickets: Map<UUID, List<Ticket>>,
                                    allProgresses: Map<UUID, Progress>,
                                    allSubtickets: Map<UUID, List<Ticket>>,
                                    allAssignments: Map<UUID, List<AssignedTicketUser>>,
                                    allDescriptions: Map<UUID, Comment>,
                                    allParentTickets: Map<UUID, Ticket>,
                                    allCreators: Map<UUID, User>,
                                    allProjects: Map<UUID, Project>,
                                    allTags: Map<UUID, List<TicketTag>>
    ): TicketResult {
        val comments = allNormalComments[t.id] ?: emptyList()
        val realCommentIds = comments.map(Comment::id)

        val referencingTickets = allReferencingTickets[t.id] ?: emptyList()
        val referencingTicketIds = referencingTickets.map(Ticket::id)

        val referencedTickets = allReferencedTickets[t.id] ?: emptyList()
        val referencedTicketIds = referencedTickets.map { it.id }

        val progress = allProgresses[t.id]

        val subtickets = allSubtickets[t.id] ?: emptyList()
        val subticketIds = subtickets.map(Ticket::id)

        val assignedUsers = allAssignments[t.id] ?: emptyList()

        val description = allDescriptions[t.id]!!

        val parent = allParentTickets[t.id]

        val creator = allCreators[t.id]!!

        val project = allProjects[t.id]!!

        val tags = allTags[t.id] ?: emptyList()

        return TicketResult(id = t.id,
                number = t.number,
                createTime = t.createTime,
                title = t.title,
                open = t.open,
                storyPoints = t.storyPoints,
                initialEstimatedTime = t.initialEstimatedTime,
                currentEstimatedTime = t.currentEstimatedTime,
                progress = ProgressResult(progress),
                dueDate = t.dueDate,
                description = description.text,
                projectId = project.id,
                ticketAssignments = assignedUsers.map(::TicketAssignmentResult),
                subTicketIds = subticketIds,
                parentTicketId = parent?.id,
                createdBy = creator.id,
                tagIds = tags.map(TicketTag::id),
                referencedTicketIds = referencedTicketIds,
                referencingTicketIds = referencingTicketIds,
                commentIds = realCommentIds)
    }
}