package io.ticktag.persistence.tickettag

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketTag
import java.util.*

@TicktagRepository
interface TicketTagRepository : TicktagCrudRepository<TicketTag, UUID> {
}