package io.ticktag.restinterface.ticketevent.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticketevent.schema.TicketEventResultJson
import io.ticktag.restinterface.ticketevent.schema.TicketEventTitleChangedResultJson
import io.ticktag.service.ticketevent.dto.TicketEventTitleChangedResult
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

    @GetMapping
    open fun listTicketEvents(@RequestParam(name = "ticketId") ticketId: UUID) : List<TicketEventResultJson> {
        return ticketEventService.listTicketEvents(ticketId).map { e ->
            when (e) {
                is TicketEventTitleChangedResult -> TicketEventTitleChangedResultJson(e)
                else -> TicketEventResultJson(e)
            }
        }
    }

}