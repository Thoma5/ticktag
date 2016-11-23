package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeAdded
import io.ticktag.persistence.ticket.entity.TimeCategory
import java.time.Duration

class TicketEventLoggedTimeAddedResult(e: TicketEventLoggedTimeAdded) : TicketEventResult(e) {
    val comment: Comment
    val category: TimeCategory
    val loggedTime: Duration

    init {
        comment = e.comment
        category = e.category
        loggedTime = e.loggedTime
    }
}