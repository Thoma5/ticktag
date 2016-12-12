package io.ticktag.service.ticket.service

import io.ticktag.service.Principal
import io.ticktag.service.ticket.dto.CreateTicket
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.dto.UpdateTicket
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.*


interface TicketService {
    fun listTickets(project: UUID,
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
                    pageable: Pageable): Page<TicketResult>

    fun listTickets(project: UUID, pageable: Pageable): Page<TicketResult>
    fun getTicket(id: UUID): TicketResult
    fun getTickets(ids: Collection<UUID>, principal: Principal): Map<UUID, TicketResult>
    fun createTicket(createTicket: CreateTicket, principal: Principal, projectId: UUID): TicketResult
    fun updateTicket(updateTicket: UpdateTicket, ticketId: UUID, principal: Principal): TicketResult
    fun deleteTicket(id: UUID)
    fun listTicketsFuzzy(project: UUID, query: String, pageable: Pageable): List<TicketResult>
}
