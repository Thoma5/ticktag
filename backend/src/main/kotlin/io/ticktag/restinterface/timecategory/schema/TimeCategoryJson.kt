package io.ticktag.restinterface.timecategory.schema

import io.ticktag.persistence.ticket.entity.LoggedTime
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeAdded
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeRemoved
import io.ticktag.service.timecategory.dto.TimeCategoryResult
import io.ticktag.service.timecategory.dto.TimeCategoryWithUsageResult
import java.util.*

data class TimeCategoryJson(
        val id: UUID,
        val pId: UUID,
        val name: String,
        val loggedTime: MutableList<LoggedTime>?,
        val loggedTimeAddedEvents: MutableList<TicketEventLoggedTimeAdded>?,
        val loggedTimeRemovedEvents: MutableList<TicketEventLoggedTimeRemoved>?
) {
    constructor(t: TimeCategoryResult) : this(id = t.id, pId = t.pId, name = t.name, loggedTime = null, loggedTimeAddedEvents = null, loggedTimeRemovedEvents = null)
    constructor(t: TimeCategoryWithUsageResult) : this(
            id = t.id,
            pId = t.pId,
            name = t.name,
            loggedTime = t.loggedTime,
            loggedTimeAddedEvents = t.loggedTimeAddedEvents,
            loggedTimeRemovedEvents = t.loggedTimeRemovedEvents
    )
}



