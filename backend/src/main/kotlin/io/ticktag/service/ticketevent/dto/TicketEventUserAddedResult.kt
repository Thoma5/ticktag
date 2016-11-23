package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.AssignmentTag
import io.ticktag.persistence.ticket.entity.TicketEventUserAdded
import io.ticktag.persistence.user.entity.User

class TicketEventUserAddedResult(e: TicketEventUserAdded) : TicketEventResult(e) {
    val addedUser: User
    val tag: AssignmentTag

    init {
        addedUser = e.addedUser
        tag = e.tag
    }
}