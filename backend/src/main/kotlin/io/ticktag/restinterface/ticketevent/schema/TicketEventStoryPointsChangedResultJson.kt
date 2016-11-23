package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.service.ticketevent.dto.TicketEventStoryPointsChangedResult

class TicketEventStoryPointsChangedResultJson(e: TicketEventStoryPointsChangedResult) : TicketEventResultJson(e) {
    val srcStoryPoints: Int?
    val dstStoryPoints: Int?

    init {
        srcStoryPoints = e.srcStoryPoints
        dstStoryPoints = e.dstStoryPoints
    }
}