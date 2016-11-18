package io.ticktag.service.timecategory.dto

import io.ticktag.persistence.ticket.entity.LoggedTime
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeAdded
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeRemoved
import io.ticktag.persistence.ticket.entity.TimeCategory
import java.util.*

data class TimeCategoryWithUsageResult(
        val id: UUID,
        val pId: UUID,
        val name: String,
        val loggedTime: MutableList<LoggedTime>,
        val loggedTimeAddedEvents: MutableList<TicketEventLoggedTimeAdded>,
        val loggedTimeRemovedEvents: MutableList<TicketEventLoggedTimeRemoved>

) {
    constructor(t: TimeCategory) : this(
            id = t.id,
            pId = t.project.id,
            name = t.name, loggedTime = t.loggedTimes,
            loggedTimeAddedEvents = t.loggedTimeAddedEvents,
            loggedTimeRemovedEvents = t.loggedTimeRemovedEvents
    )
}


