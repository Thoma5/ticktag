package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeRemoved
import java.time.Duration
import java.util.*

class TicketEventLoggedTimeRemovedResult(e: TicketEventLoggedTimeRemoved) : TicketEventResult(e) {
    val commentId: UUID
    val categoryId: UUID
    val categoryName: String
    val loggedTime: Duration

    init {
        commentId = e.comment.id
        categoryId = e.category.id
        categoryName = e.category.name
        loggedTime = e.loggedTime
    }
}