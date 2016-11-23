package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventCommentTextChangedResult
import java.util.*

class TicketEventCommentTextChangedResultJson(e: TicketEventCommentTextChangedResult) : TicketEventResultJson(e) {
    val commentId: UUID
    val srcText: String
    val dstText: String

    init {
        commentId = e.commentId
        srcText = e.srcText
        dstText = e.dstText
    }
}
