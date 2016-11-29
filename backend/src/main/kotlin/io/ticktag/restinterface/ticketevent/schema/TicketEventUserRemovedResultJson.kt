package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventUserRemovedResult
import java.util.*

class TicketEventUserRemovedResultJson(e: TicketEventUserRemovedResult) : TicketEventResultJson(e) {
    val removedUserId: UUID
    val removedUserUsername: String
    val assignmentTagId: UUID
    val assignmentTagName: String
    val assignmentTagColor: String

    init {
        removedUserId = e.removedUserId
        removedUserUsername = e.removedUserUsername
        assignmentTagId = e.assignmentTagId
        assignmentTagName = e.assignmentTagName
        assignmentTagColor = e.assignmentTagColor
    }
}