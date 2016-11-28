package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeAdded
import java.time.Duration
import java.util.*

class TicketEventLoggedTimeAddedResult(e: TicketEventLoggedTimeAdded) : TicketEventResult(e) {
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