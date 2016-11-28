package io.ticktag.restinterface.ticketevent.schema

import io.ticktag.restinterface.ticketevent.schema.TicketEventResultJson
import io.ticktag.service.ticketevent.dto.TicketEventParentChangedResult
import java.util.*

class TicketEventParentChangedResultJson(e: TicketEventParentChangedResult) : TicketEventResultJson(e) {
    val srcParentId: UUID?
    val srcParentNumber: Int?
    val srcParentTitle: String?
    val dstParentId: UUID?
    val dstParentNumber: Int?
    val dstParentTitle: String?

    init {
        srcParentId = e.srcParentId
        srcParentNumber = e.srcParentNumber
        srcParentTitle = e.srcParentTitle
        dstParentId = e.dstParentId
        dstParentNumber = e.dstParentNumber
        dstParentTitle = e.dstParentTitle
    }
}