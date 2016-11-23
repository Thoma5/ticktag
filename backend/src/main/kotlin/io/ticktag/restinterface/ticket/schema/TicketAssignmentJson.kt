package io.ticktag.restinterface.ticket.schema

import java.util.*

data class TicketAssignmentJson(
        val assignmentTagId: UUID,
        val userId: UUID
)
