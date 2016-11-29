package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventTagAdded
import java.util.*

class TicketEventTagAddedResult(e: TicketEventTagAdded) : TicketEventResult(e) {
    val tagId: UUID

    init {
        tagId = e.tag.id
    }
}