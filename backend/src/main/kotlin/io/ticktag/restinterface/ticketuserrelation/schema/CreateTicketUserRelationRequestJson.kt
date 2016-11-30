package io.ticktag.restinterface.ticketuserrelation.schema

import java.util.*

data class CreateTicketUserRelationRequestJson(
        val assignmentTagId: UUID,
        val userId: UUID
)
