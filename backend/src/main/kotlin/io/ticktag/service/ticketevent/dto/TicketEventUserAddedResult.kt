package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventUserAdded
import java.util.*

class TicketEventUserAddedResult(e: TicketEventUserAdded) : TicketEventResult(e) {
    val addedUserId: UUID
    val assignmentTagId: UUID

    init {
        addedUserId = e.addedUser.id
        assignmentTagId = e.tag.id
    }
}