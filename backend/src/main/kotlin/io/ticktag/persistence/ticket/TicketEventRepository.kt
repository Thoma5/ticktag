package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketEvent
import java.util.*

@TicktagRepository
interface TicketEventRepository : TicktagCrudRepository<TicketEvent, UUID> {
    fun findByTicketId(ticketId: UUID): List<TicketEvent>
}