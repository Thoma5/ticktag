package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventCommentTextChanged
import java.util.*

class TicketEventCommentTextChangedResult(e: TicketEventCommentTextChanged) : TicketEventResult(e) {
    val commentId: UUID
    val srcText: String
    val dstText: String

    init {
        commentId = e.comment.id
        srcText = e.srcText
        dstText = e.dstText
    }
}