package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventUserAddedResult
import java.util.*

class TicketEventUserAddedResultJson(e: TicketEventUserAddedResult) : TicketEventResultJson(e) {
    val addedUserId: UUID
    val assignmentTagId: UUID

    init {
        addedUserId = e.addedUserId
        assignmentTagId = e.assignmentTagId
    }
}