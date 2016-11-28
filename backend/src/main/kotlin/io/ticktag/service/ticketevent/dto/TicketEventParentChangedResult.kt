package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventParentChanged
import java.util.*

class TicketEventParentChangedResult(e: TicketEventParentChanged) : TicketEventResult(e) {
    val srcParentId: UUID?
    val srcParentNumber: Int?
    val srcParentTitle: String?
    val dstParentId: UUID?
    val dstParentNumber: Int?
    val dstParentTitle: String?

    init {
        srcParentId = e.srcParent?.id
        srcParentNumber = e.srcParent?.number
        srcParentTitle = e.srcParent?.title
        dstParentId = e.dstParent?.id
        dstParentNumber = e.dstParent?.number
        dstParentTitle = e.dstParent?.title
    }
}