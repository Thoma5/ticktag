package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventUserRemoved
import java.util.*

class TicketEventUserRemovedResult(e: TicketEventUserRemoved) : TicketEventResult(e) {
    val removedUserId: UUID
    val assignmentTagId: UUID

    init {
        removedUserId = e.removedUser.id
        assignmentTagId = e.tag.id
    }
}