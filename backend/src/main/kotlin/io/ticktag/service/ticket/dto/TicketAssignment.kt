package io.ticktag.service.ticket.dto

import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.restinterface.ticketuserrelation.schema.CreateTicketUserRelationRequestJson
import java.util.*

data class TicketAssignment(
        val assignmentTagId: UUID,
        val userId: UUID
) {
    constructor(req: CreateTicketUserRelationRequestJson) : this(
            assignmentTagId = req.assignmentTagId, userId = req.userId)

    constructor(req: AssignedTicketUser) : this(
            assignmentTagId = req.tag.id, userId = req.user.id)
}