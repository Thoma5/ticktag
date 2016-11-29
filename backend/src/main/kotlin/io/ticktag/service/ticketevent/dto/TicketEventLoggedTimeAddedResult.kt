package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeAdded
import java.time.Duration
import java.util.*

class TicketEventLoggedTimeAddedResult(e: TicketEventLoggedTimeAdded) : TicketEventResult(e) {
    val commentId: UUID
    val categoryId: UUID
    val loggedTime: Duration

    init {
        commentId = e.comment.id
        categoryId = e.category.id
        loggedTime = e.loggedTime
    }
}