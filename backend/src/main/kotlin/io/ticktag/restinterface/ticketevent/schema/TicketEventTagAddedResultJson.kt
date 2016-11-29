package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventTagAddedResult
import java.util.*

class TicketEventTagAddedResultJson(e: TicketEventTagAddedResult) : TicketEventResultJson(e) {
    val tagId: UUID

    init {
        tagId = e.tagId
    }
}