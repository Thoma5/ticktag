package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEvent
import java.time.Instant
import java.util.*

open class TicketEventResult(
        val id: UUID,
        val userId: UUID,
        val ticketId: UUID,
        val time: Instant
) {
    constructor(e: TicketEvent) : this(id = e.id, userId = e.user.id, ticketId = e.ticket.id, time = e.time)
}