package io.ticktag.restinterface.ticket.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticket.schema.TicketResultJson
import io.ticktag.restinterface.user.schema.CreateTicketRequestJson
import io.ticktag.service.project.dto.CreateTicket
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.service.TicketService
import org.springframework.web.bind.annotation.*
import java.util.*
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

    @GetMapping(value = "/{id}")
    open fun getTicket(@PathVariable(name = "id") id: UUID ):TicketResultJson {
        return TicketResultJson(ticketService.getTicket(id))
    }

    /*@PostMapping
    open fun createTicket(@RequestBody req: CreateTicketRequestJson): TicketResultJson {
        val ticket = ticketService.createTicket(CreateTicket(
                req.id,req.number,req.createTime,req.title,req.open,req.storyPoints,req.initialEstimatedTime,
                req.currentEstimatedTime,req.dueDate,req.description,req.projectID))
        return TicketResultJson(ticket)
    }*/
}