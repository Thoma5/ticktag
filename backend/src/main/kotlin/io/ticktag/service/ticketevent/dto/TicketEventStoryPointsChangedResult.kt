package io.ticktag.service.ticketevent.dto

import io.ticktag.persistence.ticket.entity.TicketEventStoryPointsChanged

class TicketEventStoryPointsChangedResult(e: TicketEventStoryPointsChanged) : TicketEventResult(e) {
    val srcStoryPoints: Int?
    val dstStoryPoints: Int?

    init {
        srcStoryPoints = e.srcStoryPoints
        dstStoryPoints = e.dstStoryPoints
    }
}