package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.Ticket
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

@TicktagRepository
interface TicketFilterRepository : TicktagCrudRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket>