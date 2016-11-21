package io.ticktag.service.member.dto

import java.util.*

data class TicketAssignmentResultJson(
        val ticketId: UUID,
        val assignmentTagId: UUID,
        val userId: UUID
) {
    constructor(t: TicketAssignmentResult) : this(ticketId = t.ticketId, assignmentTagId = t.assignmentTagId, userId = t.userId)
}
