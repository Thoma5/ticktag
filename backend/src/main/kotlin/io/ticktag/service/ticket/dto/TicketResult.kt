package io.ticktag.service.ticket.dto

import io.ticktag.persistence.ticket.entity.Ticket
import java.util.*


data class TicketResult(
        val id: UUID
) {
    constructor(t: Ticket) : this(id = t.id)
}
