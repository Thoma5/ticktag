package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventParentChanged
import java.util.*

class TicketEventParentChangedResult(e: TicketEventParentChanged) : TicketEventResult(e) {
    val srcParentId: UUID?
    val dstParentId: UUID?

    init {
        srcParentId = e.srcParent?.id
        dstParentId = e.dstParent?.id
    }
}