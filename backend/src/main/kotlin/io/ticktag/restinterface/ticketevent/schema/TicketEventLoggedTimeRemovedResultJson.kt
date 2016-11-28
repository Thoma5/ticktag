package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventLoggedTimeRemovedResult
import java.time.Duration
import java.util.*

class TicketEventLoggedTimeRemovedResultJson(e: TicketEventLoggedTimeRemovedResult) : TicketEventResultJson(e) {
    val commentId: UUID
    val categoryId: UUID
    val categoryName: String
    val loggedTime: Duration

    init {
        commentId = e.commentId
        categoryId = e.categoryId
        categoryName = e.categoryName
        loggedTime = e.loggedTime
    }
}