package io.ticktag.persistence.ticket.entity

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository

@TicktagRepository
interface TicketAssignmentRepository : TicktagCrudRepository<AssignedTicketUser, AssignedTicketUserKey> {
}
