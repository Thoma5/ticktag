package io.ticktag.restinterface.ticket.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticket.schema.TicketResultJson
import io.ticktag.restinterface.user.schema.CreateTicketRequestJson
import io.ticktag.service.Principal
import io.ticktag.service.project.dto.CreateTicket
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.service.TicketService
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    open fun listTickets(@RequestParam(name = "projectId") req: UUID): List<TicketResultJson> {
        return ticketService.listTickets(req).map(::TicketResultJson)
    }

    @GetMapping(value = "/{id}")
    open fun getTicket(@PathVariable(name = "id") id: UUID ):TicketResultJson {
        return TicketResultJson(ticketService.getTicket(id))
    }

    @PostMapping
    open fun createTicket(@RequestBody req: CreateTicketRequestJson,
                          @AuthenticationPrincipal principal: Principal): TicketResultJson {
        val ticket = ticketService.createTicket(CreateTicket(
                req.number,req.createTime,req.title,req.open,req.storyPoints,req.initialEstimatedTime,
                req.currentEstimatedTime,req.dueDate,req.description,req.projectID),principal)
        return TicketResultJson(ticket)
    }
}