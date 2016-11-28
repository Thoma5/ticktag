package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventUserRemoved
import java.util.*

class TicketEventUserRemovedResult(e: TicketEventUserRemoved) : TicketEventResult(e) {
    val removedUserId: UUID
    val removedUserUsername: String
    val assignmentTagId: UUID
    val assignmentTagName: String
    val assignmentTagColor: String

    init {
        removedUserId = e.removedUser.id
        removedUserUsername = e.removedUser.username
        assignmentTagId = e.tag.id
        assignmentTagName = e.tag.name
        assignmentTagColor = e.tag.color
    }
}