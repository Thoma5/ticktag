package io.ticktag.restinterface.ticketevent.schema

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.ticktag.service.ticketevent.dto.TicketEventResult
import java.util.*

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
open class TicketEventResultJson(
        val id: UUID,
        val userId: UUID,
        val ticketId: UUID
) {
    constructor(e: TicketEventResult) : this(id = e.id, userId = e.userId, ticketId = e.ticketId)
}