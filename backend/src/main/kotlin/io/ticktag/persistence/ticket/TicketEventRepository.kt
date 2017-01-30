package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketEvent
import io.ticktag.persistence.ticket.entity.TicketEventStateChanged
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface TicketEventRepository : TicktagCrudRepository<TicketEvent, UUID> {
    fun findByTicketIdOrderByTimeAsc(ticketId: UUID): List<TicketEvent>
}

@TicktagRepository
interface TicketEventStateChangedRepository : TicktagCrudRepository<TicketEventStateChanged, UUID> {
    fun findByTicketIdIn(ticketIds: List<UUID>): List<TicketEventStateChanged>
}
