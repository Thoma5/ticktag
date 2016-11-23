package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventUserAdded
import java.util.*

class TicketEventUserAddedResult(e: TicketEventUserAdded) : TicketEventResult(e) {
    val addedUserId: UUID
    val addedUserUsername: String
    val assignmentTagId: UUID
    val assignmentTagName: String
    val assignmentTagColor: String

    init {
        addedUserId = e.addedUser.id
        addedUserUsername = e.addedUser.username
        assignmentTagId = e.tag.id
        assignmentTagName = e.tag.name
        assignmentTagColor = e.tag.color
    }
}