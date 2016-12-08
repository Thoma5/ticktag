package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketEvent
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface TicketEventRepository : TicktagCrudRepository<TicketEvent, UUID> {
    fun findByTicketIdOrderByTimeAsc(ticketId: UUID): List<TicketEvent>
   // @Query("select ticket_event.id, ticket_id, user_id, time, src_state, dst_state from TicketEvent join TicketEventStateChanged where ticket_id in :ids")
   // fun findAllStateChangedEvents(@Param("ids") ticketIds: List<UUID>): List<TicketEvent>

    fun findByTicketIdIn(ticketIds: List<UUID>): List<TicketEvent>
}