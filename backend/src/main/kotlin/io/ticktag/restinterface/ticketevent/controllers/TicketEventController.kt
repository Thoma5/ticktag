package io.ticktag.restinterface.ticketevent.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticketevent.schema.*
import io.ticktag.service.ticketevent.dto.*
import io.ticktag.service.ticketevent.services.TicketEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/ticketevent")
@Api(tags = arrayOf("ticketevent"), description = "ticketevent management")
open class TicketEventController @Inject constructor(
        private val ticketEventService: TicketEventService
) {

    @GetMapping(value = "/statechangedevent")
    open fun listTicketStateChangedEvents(@RequestParam(name = "ticketIds") ticketIds: List<UUID>):List<TicketEventResultJson> {
        return ticketEventService.findAllStateChangedEvents(ticketIds).map { e ->
            when (e) {
                is TicketEventStateChangedResult -> TicketEventStateChangedResultJson(e)
                else -> TicketEventResultJson(e)
            }
        }
    }

    @GetMapping
    open fun listTicketEvents(@RequestParam(name = "ticketId") ticketId: UUID) : List<TicketEventResultJson> {
        return ticketEventService.listTicketEvents(ticketId).map { e ->
            when (e) {
                is TicketEventTitleChangedResult -> TicketEventTitleChangedResultJson(e)
                is TicketEventCommentTextChangedResult -> TicketEventCommentTextChangedResultJson(e)
                is TicketEventCurrentEstimatedTimeChangedResult -> TicketEventCurrentEstimatedTimeChangedResultJson(e)
                is TicketEventDueDateChangedResult -> TicketEventDueDateChangedResultJson(e)
                is TicketEventInitialEstimatedTimeChangedResult -> TicketEventInitialEstimatedTimeChangedResultJson(e)
                is TicketEventLoggedTimeAddedResult -> TicketEventLoggedTimeAddedResultJson(e)
                is TicketEventLoggedTimeRemovedResult -> TicketEventLoggedTimeRemovedResultJson(e)
                is TicketEventMentionAddedResult -> TicketEventMentionAddedResultJson(e)
                is TicketEventMentionRemovedResult -> TicketEventMentionRemovedResultJson(e)
                is TicketEventParentChangedResult -> TicketEventParentChangedResultJson(e)
                is TicketEventStateChangedResult -> TicketEventStateChangedResultJson(e)
                is TicketEventStoryPointsChangedResult -> TicketEventStoryPointsChangedResultJson(e)
                is TicketEventTagAddedResult -> TicketEventTagAddedResultJson(e)
                is TicketEventTagRemovedResult -> TicketEventTagRemovedResultJson(e)
                is TicketEventUserAddedResult -> TicketEventUserAddedResultJson(e)
                is TicketEventUserRemovedResult -> TicketEventUserRemovedResultJson(e)
                else -> TicketEventResultJson(e)
            }
        }
    }

}