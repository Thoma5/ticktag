package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventTagRemoved
import java.util.*

class TicketEventTagRemovedResult(e: TicketEventTagRemoved) : TicketEventResult(e) {
    val tagId: UUID
    val tagName: String
    val tagColor: String

    init {
        tagId = e.tag.id
        tagName = e.tag.name
        tagColor = e.tag.color
    }
}