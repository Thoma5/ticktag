package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeRemoved
import java.time.Duration
import java.util.*

class TicketEventLoggedTimeRemovedResult(e: TicketEventLoggedTimeRemoved) : TicketEventResult(e) {
    val commentId: UUID
    val categoryId: UUID
    val loggedTime: Duration

    init {
        commentId = e.comment.id
        categoryId = e.category.id
        loggedTime = e.loggedTime
    }
}