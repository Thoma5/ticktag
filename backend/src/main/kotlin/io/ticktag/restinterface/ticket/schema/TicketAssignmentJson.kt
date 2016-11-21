package io.ticktag.restinterface.user.schema

import java.util.*

data class TicketAssignmentJson(
        val assignmentTagId: UUID,
        val userId: UUID
)
