package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeRemoved
import io.ticktag.persistence.ticket.entity.TimeCategory
import java.time.Duration

class TicketEventLoggedTimeRemovedResult(e: TicketEventLoggedTimeRemoved) : TicketEventResult(e) {
    val comment: Comment
    val category: TimeCategory
    val loggedTime: Duration

    init {
        comment = e.comment
        category = e.category
        loggedTime = e.loggedTime
    }
}