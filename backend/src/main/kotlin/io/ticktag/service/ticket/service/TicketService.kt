package io.ticktag.service.ticket.service

import io.ticktag.service.project.dto.CreateTicket
import io.ticktag.service.ticket.dto.TicketResult
import java.util.*


interface TicketService {
    fun listTickets(): List<TicketResult>
    fun getTicket(id:UUID): TicketResult
    fun createTicket(createTicket: CreateTicket):TicketResult
}