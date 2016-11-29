package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventUserRemovedResult
import java.util.*

class TicketEventUserRemovedResultJson(e: TicketEventUserRemovedResult) : TicketEventResultJson(e) {
    val removedUserId: UUID
    val assignmentTagId: UUID

    init {
        removedUserId = e.removedUserId
        assignmentTagId = e.assignmentTagId
    }
}