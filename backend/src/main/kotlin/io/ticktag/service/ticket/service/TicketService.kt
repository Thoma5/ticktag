package io.ticktag.service.ticket.service

import io.ticktag.service.Principal
import io.ticktag.service.ticket.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.*


interface TicketService {
    /**
     * List all Tickets for a project with given filters
     */
    fun listTicketsOverview(project: UUID,
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
                    pageable: Pageable): Page<TicketOverviewResult>

    /**
     * List Tickets with paging
     */
    fun listTickets(project: UUID, pageable: Pageable): Page<TicketResult>

    /**
     * Get one Ticket
     */
    fun getTicket(id: UUID): TicketResult

    /**
     * Get a ticket with the ticket id, which is unique for each project
     */
    fun getTicket(projectId: UUID, ticketNumber: Int): TicketResult

    /**
     * get all tickets for the supplied ids.
     */
    fun getTickets(ids: Collection<UUID>, principal: Principal): Map<UUID, TicketResult>

    /**
     * create a ticket
     */
    fun createTicket(createTicket: CreateTicket, principal: Principal, projectId: UUID): TicketResult

    /**
     * update a ticket with Properties encapsulated in an Object
     */
    fun updateTicket(updateTicket: UpdateTicket, ticketId: UUID, principal: Principal): TicketResult

    /**
     * Delete a ticket
     */
    fun deleteTicket(id: UUID)

    /**
     * list a few Tickets. this function will be used for autocompletion
     */
    fun listTicketsFuzzy(project: UUID, query: String, pageable: Pageable): List<TicketResult>

    /**
     * List tickets with story points. Similar to listTicketOverview but only for Storypoints
     */
    fun listTicketsStoryPoints(project: UUID,
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
                               parent: Int?): List<TicketStoryPointResult>
}


