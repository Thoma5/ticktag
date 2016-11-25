package io.ticktag.service.ticket.service

import io.ticktag.service.Principal
import io.ticktag.service.ticket.dto.CreateTicket
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.dto.UpdateTicket
import org.springframework.data.domain.Pageable
import java.util.*


interface TicketService {
    fun listTickets(project: UUID, pageable: Pageable): List<TicketResult>
    fun getTicket(id: UUID): TicketResult
    fun createTicket(createTicket: CreateTicket, principal: Principal, projectId: UUID): TicketResult
    fun updateTicket(updateTicket: UpdateTicket, ticketId: UUID, principal: Principal): TicketResult
    fun deleteTicket(id: UUID)
    fun listTicketsFuzzy(project: UUID, query: String, pageable: Pageable): List<TicketResult>
}
