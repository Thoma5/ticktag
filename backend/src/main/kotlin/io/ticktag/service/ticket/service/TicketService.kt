package io.ticktag.service.ticket.service

import io.ticktag.service.Principal
import io.ticktag.service.project.dto.CreateTicket
import io.ticktag.service.project.dto.UpdateTicket
import io.ticktag.service.ticket.dto.TicketResult
import java.util.*


interface TicketService {
    fun listTickets(project: UUID): List<TicketResult>
    fun getTicket(id:UUID): TicketResult
    fun createTicket(createTicket: CreateTicket,principal: Principal,projectId: UUID):TicketResult
    fun updateTicket(updateTicket: UpdateTicket,ticketId:UUID,principal: Principal):TicketResult
    fun deleteTicket(id:UUID)
}