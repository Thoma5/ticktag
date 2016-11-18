package io.ticktag.persistence.ticket.entity

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import java.util.*

@TicktagRepository
interface TicketRepository : TicktagCrudRepository<Ticket, UUID> {
    fun findByProjectId(projectId: UUID):List<Ticket>
}
