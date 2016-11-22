package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventCommentTextChanged
import java.util.*

class TicketEventCommentTextChangedResult(e: TicketEventCommentTextChanged) : TicketEventResult(e) {
    val comment_id: UUID
    val srcText: String
    val dstText: String

    init {
        comment_id = e.comment.id
        srcText = e.srcText
        dstText = e.dstText
    }
}