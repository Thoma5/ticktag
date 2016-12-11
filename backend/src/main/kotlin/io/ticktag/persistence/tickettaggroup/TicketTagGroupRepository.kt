package io.ticktag.persistence.tickettaggroup

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketTagGroup
import java.util.*

@TicktagRepository
interface TicketTagGroupRepository : TicktagCrudRepository<TicketTagGroup, UUID>