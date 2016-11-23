package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventTagRemovedResult
import java.util.*

class TicketEventTagRemovedResultJson(e: TicketEventTagRemovedResult) : TicketEventResultJson(e) {
    val tagId: UUID
    val tagName: String
    val tagColor: String

    init {
        tagId = e.tagId
        tagName = e.tagName
        tagColor = e.tagColor
    }
}