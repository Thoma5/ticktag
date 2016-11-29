package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventTagRemovedResult
import java.util.*

class TicketEventTagRemovedResultJson(e: TicketEventTagRemovedResult) : TicketEventResultJson(e) {
    val tagId: UUID

    init {
        tagId = e.tagId
    }
}