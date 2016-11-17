package io.ticktag.persistence.tickettag

import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketTag
import java.util.*

interface TicketTagRepository : TicktagCrudRepository<TicketTag, UUID> {
}