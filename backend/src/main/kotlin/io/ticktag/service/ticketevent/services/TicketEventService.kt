package io.ticktag.service.ticketevent.services

import io.ticktag.service.ticketevent.dto.TicketEventResult
import java.util.*

interface TicketEventService {
    fun listTicketEvents(ticketId: UUID): List<TicketEventResult>
    fun findAllStateChangedEvents(ticketIds: List<UUID>): List<TicketEventResult>
}