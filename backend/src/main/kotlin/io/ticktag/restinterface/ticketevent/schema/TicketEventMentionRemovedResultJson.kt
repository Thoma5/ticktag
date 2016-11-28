package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventMentionRemovedResult
import java.util.*

class TicketEventMentionRemovedResultJson(e: TicketEventMentionRemovedResult) : TicketEventResultJson(e) {
    val commentId: UUID
    val mentionedTicketId: UUID
    val mentionedTicketNumber: Int
    val mentionedTicketTitle: String

    init {
        commentId = e.commentId
        mentionedTicketId = e.mentionedTicketId
        mentionedTicketNumber = e.mentionedTicketNumber
        mentionedTicketTitle = e.mentionedTicketTitle
    }
}