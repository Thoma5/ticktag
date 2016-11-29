package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventUserAddedResult
import java.util.*

class TicketEventUserAddedResultJson(e: TicketEventUserAddedResult) : TicketEventResultJson(e) {
    val addedUserId: UUID
    val addedUserUsername: String
    val assignmentTagId: UUID
    val assignmentTagName: String
    val assignmentTagColor: String

    init {
        addedUserId = e.addedUserId
        addedUserUsername = e.addedUserUsername
        assignmentTagId = e.assignmentTagId
        assignmentTagName = e.assignmentTagName
        assignmentTagColor = e.assignmentTagColor
    }
}