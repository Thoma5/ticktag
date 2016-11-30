package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventTagRemoved
import java.util.*

class TicketEventTagRemovedResult(e: TicketEventTagRemoved) : TicketEventResult(e) {
    val tagId: UUID

    init {
        tagId = e.tag.id
    }
}