package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventTagAddedResult
import java.util.*

class TicketEventTagAddedResultJson(e: TicketEventTagAddedResult) : TicketEventResultJson(e) {
    val tagId: UUID
    val tagName: String
    val tagColor: String

    init {
        tagId = e.tagId
        tagName = e.tagName
        tagColor = e.tagColor
    }
}