package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventTagAdded
import java.util.*

class TicketEventTagAddedResult(e: TicketEventTagAdded) : TicketEventResult(e) {
    val tagId: UUID
    val tagName: String
    val tagColor: String

    init {
        tagId = e.tag.id
        tagName = e.tag.name
        tagColor = e.tag.color
    }
}