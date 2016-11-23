package io.ticktag.service.ticketassignment.dto

import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import java.util.*

data class TicketAssignmentResult(
        val ticketId: UUID,
        val assignmentTagId: UUID,
        val userId: UUID
) {
    constructor(a: AssignedTicketUser) : this(ticketId = a.ticket.id, assignmentTagId = a.tag.id, userId = a.user.id)
}
