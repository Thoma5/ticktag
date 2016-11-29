package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventLoggedTimeAddedResult
import java.time.Duration
import java.util.*

class TicketEventLoggedTimeAddedResultJson(e: TicketEventLoggedTimeAddedResult) : TicketEventResultJson(e) {
    val commentId: UUID
    val categoryId: UUID
    val loggedTime: Duration

    init {
        commentId = e.commentId
        categoryId = e.categoryId
        loggedTime = e.loggedTime
    }
}