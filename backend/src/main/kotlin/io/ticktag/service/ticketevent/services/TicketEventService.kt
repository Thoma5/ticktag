package io.ticktag.service.ticketevent.services

import io.ticktag.service.Principal
import io.ticktag.service.ticketevent.dto.TicketEventResult
import java.util.*

interface TicketEventService {
    /**
     * List ticket events (History of a Ticket) for a given Ticket ID
     */
    fun listTicketEvents(ticketId: UUID): List<TicketEventResult>

    /**
     * List all Events regarding a state change
     */
    fun findAllStateChangedEvents(ticketIds: List<UUID>, principal: Principal): List<TicketEventResult>
}