package io.ticktag.restinterface.ticket.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticket.schema.*
import io.ticktag.service.Principal
import io.ticktag.service.ticket.dto.CreateTicket
import io.ticktag.service.ticket.dto.UpdateTicket
import io.ticktag.service.ticket.service.TicketService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/ticket")
@Api(tags = arrayOf("ticket"), description = "ticket management")
open class TicketController @Inject constructor(
        private val ticketService: TicketService
) {

    @GetMapping
    open fun listTickets(@RequestParam(name = "projectId") req: UUID,
                         @RequestParam(name = "page", defaultValue = "0", required = false) page: Int,
                         @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                         @RequestParam(name = "order", required = true) order: List<TicketSort>): Page<TicketResultJson> {

        val pageRequest = PageRequest(page, size, Sort(order.map { it.order }))
        val page = ticketService.listTickets(req, pageRequest)
        val content = page.content.map(::TicketResultJson)
        return PageImpl(content,pageRequest,page.totalElements)
    }


    @GetMapping(value = "/{id}")
    open fun getTicket(@PathVariable(name = "id") id: UUID): TicketResultJson {
        return TicketResultJson(ticketService.getTicket(id))
    }

    @PostMapping
    open fun createTicket(@RequestBody req: CreateTicketRequestJson,
                          @AuthenticationPrincipal principal: Principal): TicketResultJson {
        val ticket = ticketService.createTicket(CreateTicket(req), principal, req.projectId)
        return TicketResultJson(ticket)
    }

    @PutMapping(value = "/{id}")
    open fun updateTicket(@RequestBody req: UpdateTicketRequestJson,
                          @PathVariable(name = "id") id: UUID,
                          @AuthenticationPrincipal principal: Principal): TicketResultJson {
        val ticket = ticketService.updateTicket(UpdateTicket(req), id, principal)
        return TicketResultJson(ticket)
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteTicket(@PathVariable(name = "id") id: UUID) {
        ticketService.deleteTicket(id)
    }

    @GetMapping("/fuzzy")
    open fun listTicketsFuzzy(
            @RequestParam(name="projectId", required = true) projectId: UUID,
            @RequestParam(name="q", required = true) query: String,
            @RequestParam(name="order", required = true) order: List<TicketSort>): List<TicketResultJson> {
        val tickets = ticketService.listTicketsFuzzy(projectId, query, PageRequest(0, 15, Sort(order.map { it.order })))
        return tickets.map(::TicketResultJson)
    }
}