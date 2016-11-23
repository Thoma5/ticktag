package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.AssignmentTag
import io.ticktag.persistence.ticket.entity.TicketEventUserRemoved
import io.ticktag.persistence.user.entity.User

class TicketEventUserRemovedResult(e: TicketEventUserRemoved) : TicketEventResult(e) {
    val removedUser: User
    val tag: AssignmentTag

    init {
        removedUser = e.removedUser
        tag = e.tag
    }
}