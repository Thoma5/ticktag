package io.ticktag.service.ticketevent.services

import io.ticktag.persistence.ticket.entity.TicketEvent
import java.util.*

interface TicketEventService {
    fun listTicketEvents(ticketId: UUID): List<TicketEvent>
}