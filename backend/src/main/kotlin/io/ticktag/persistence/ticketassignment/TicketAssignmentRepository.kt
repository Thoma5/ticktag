package io.ticktag.persistence.ticketassignment

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.AssignedTicketUserKey

@TicktagRepository
interface TicketAssignmentRepository : TicktagCrudRepository<AssignedTicketUser, AssignedTicketUserKey>
