package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.Comment
import io.ticktag.persistence.ticket.entity.TicketEventCommentTextChanged
import io.ticktag.persistence.ticket.entity.TicketEventTitleChanged
import java.util.*

class TicketEventCommentTextChangedResult(e: TicketEventCommentTextChanged) : TicketEventResult(e) {
    val comment: Comment
    val srcText: String
    val dstText: String

    init {
        comment = e.comment
        srcText = e.srcText
        dstText = e.dstText
    }
}