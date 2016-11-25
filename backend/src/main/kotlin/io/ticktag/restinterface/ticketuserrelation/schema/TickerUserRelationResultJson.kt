package io.ticktag.restinterface.ticketuserrelation.schema

import io.ticktag.service.ticketassignment.dto.TicketAssignmentResult
import java.util.*

data class TickerUserRelationResultJson(
        val ticketId: UUID,
        val assignmentTagId: UUID,
        val userId: UUID
) {
    constructor(t: TicketAssignmentResult) : this(ticketId = t.ticketId, assignmentTagId = t.assignmentTagId, userId = t.userId)
}
