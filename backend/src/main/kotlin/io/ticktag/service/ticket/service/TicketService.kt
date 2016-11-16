package io.ticktag.service.ticket.service

import io.ticktag.service.ticket.dto.TicketResult


interface TicketService {
    fun listTickets(): List<TicketResult>
}