package io.ticktag.persistence.tickettaggroup

import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketTagGroup
import java.util.*

interface TicketTagGroupRepository : TicktagCrudRepository<TicketTagGroup, UUID> {
}