package io.ticktag.restinterface.ticket.schema

import io.ticktag.restinterface.ticketuserrelation.schema.TicketUserRelationResultJson
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.dto.TicketStoryPointResult
import java.time.Duration
import java.time.Instant
import java.util.*

data class TicketStoryPointResultJson(
        val id: UUID,
        val open: Boolean,
        val storyPoints: Int?
) {
    constructor(t: TicketStoryPointResult) : this(id = t.id, open = t.open, storyPoints = t.storyPoints)
}