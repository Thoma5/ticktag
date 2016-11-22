package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventCommentTextChangedResult
import java.util.*

class TicketEventCommentTextChangedResultJson(e: TicketEventCommentTextChangedResult) : TicketEventResultJson(e) {
    val comment_id: UUID
    val srcText: String
    val dstText: String

    init {
        comment_id = e.comment_id
        srcText = e.srcText
        dstText = e.dstText
    }
}
