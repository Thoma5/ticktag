package io.ticktag.restinterface.ticket.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticket.schema.TicketResultJson
import io.ticktag.service.ticket.service.TicketService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/tickets")
@Api(tags = arrayOf("ticket"), description = "ticket manager")
open class TicketController@Inject constructor(
        private val ticketService: TicketService
) {

    @GetMapping
    open fun listTickets(): List<TicketResultJson> {
        return ticketService.listTickets().map(::TicketResultJson)
    }
}