package io.ticktag.service.member.dto

import java.util.*

data class TicketAssignmentResultJson(
        val ticketId: UUID,
        val tagId: UUID,
        val userId: UUID
) {
    constructor(t: TicketAssignmentResult) : this(ticketId = t.ticketId, tagId = t.tagId, userId = t.userId)
}
