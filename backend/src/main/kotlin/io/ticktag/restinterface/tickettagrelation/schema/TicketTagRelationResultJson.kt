package io.ticktag.restinterface.tickettagrelation.schema

import io.ticktag.service.tickettagrelation.dto.TicketTagRelationResult
import java.util.*

data class TicketTagRelationResultJson(
        val ticketId: UUID,
        val tagId: UUID
) {
    constructor(t: TicketTagRelationResult) : this(ticketId = t.ticketId, tagId = t.tagId)
}
