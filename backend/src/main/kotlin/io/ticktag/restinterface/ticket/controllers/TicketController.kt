package io.ticktag.restinterface.ticket.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.ticket.schema.*
import io.ticktag.service.Principal
import io.ticktag.service.ticket.dto.CreateTicket
import io.ticktag.service.ticket.service.TicketService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/ticket")
@Api(tags = arrayOf("ticket"), description = "ticket management")
open class TicketController @Inject constructor(
        private val ticketService: TicketService
) {

    @GetMapping
    open fun listTickets(@RequestParam(name = "projectId") projectId: UUID,
                         @RequestParam(name = "numbers", required = false) numbers: List<Int>?,
                         @RequestParam(name = "title", required = false) title: String?,
                         @RequestParam(name = "tags", required = false) tags: List<String>?,
                         @RequestParam(name = "users", required = false) user: List<String>?,
                         @RequestParam(name = "progressOne", required = false) progressOne: Float?,
                         @RequestParam(name = "progressTwo", required = false) progressTwo: Float?,
                         @RequestParam(name = "progressGreater", required = false) progressGreater: Boolean?,
                         @RequestParam(name = "dueDateOne", required = false) dueDateOne: Instant?,
                         @RequestParam(name = "dueDateTwo", required = false) dueDateTwo: Instant?,
                         @RequestParam(name = "dueDateGreater", required = false) dueDateGreater: Boolean?,
                         @RequestParam(name = "storyPointsOne", required = false) storyPointsOne: Int?,
                         @RequestParam(name = "storyPointsTwo", required = false) storyPointsTwo: Int?,
                         @RequestParam(name = "storyPointsGreater", required = false) storyPointsGreater: Boolean?,
                         @RequestParam(name = "open", required = false) open: Boolean?,
                         @RequestParam(name = "parent", required = false) parent: Int?,
                         @RequestParam(name = "page", defaultValue = "0", required = false) pageNumber: Int,
                         @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                         @RequestParam(name = "order", required = true) order: List<TicketSort>): Page<TicketOverviewResultJson> {

        val pageRequest = PageRequest(pageNumber, size, Sort(order.map { it.order }))
        val page = ticketService.listTicketsOverview(projectId, numbers, title, tags, user, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo, storyPointsGreater, open, parent, pageRequest)
        val content = page.content.map(::TicketOverviewResultJson)
        return PageImpl(content, pageRequest, page.totalElements)
    }


    @GetMapping(value = "/{id}")
    open fun getTicket(@PathVariable(name = "id") id: UUID): TicketResultJson {
        return TicketResultJson(ticketService.getTicket(id))
    }

    @GetMapping(value = "/{projectId}/{ticketNumber}")
    open fun getTicketByNumber(@PathVariable("projectId") projectId: UUID, @PathVariable("ticketNumber") ticketNumber: Int): TicketResultJson {
        return TicketResultJson(ticketService.getTicket(projectId, ticketNumber))
    }

    @PostMapping
    open fun createTicket(@RequestBody req: CreateTicketRequestJson,
                          @AuthenticationPrincipal principal: Principal): ResponseEntity<TicketResultJson> {
        val dto = toCreateDto(req) ?: return ResponseEntity.badRequest().body(null)
        val ticket = ticketService.createTicket(dto, principal, req.projectId)
        return ResponseEntity.ok(TicketResultJson(ticket))
    }

    @PutMapping(value = "/{id}")
    open fun updateTicket(@RequestBody req: UpdateTicketRequestJson,
                          @PathVariable(name = "id") id: UUID,
                          @AuthenticationPrincipal principal: Principal): TicketResultJson {
        val ticket = ticketService.updateTicket(req.toUpdateTicket(), id, principal)
        return TicketResultJson(ticket)
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteTicket(@PathVariable(name = "id") id: UUID) {
        ticketService.deleteTicket(id)
    }

    @GetMapping("/fuzzy")
    open fun listTicketsFuzzy(
            @RequestParam(name = "projectId", required = true) projectId: UUID,
            @RequestParam(name = "q", required = true) query: String,
            @RequestParam(name = "order", required = true) order: List<TicketSort>): List<TicketResultJson> {
        val tickets = ticketService.listTicketsFuzzy(projectId, query, PageRequest(0, 15, Sort(order.map { it.order })))
        return tickets.map(::TicketResultJson)
    }

    @GetMapping("/storypoints")
    open fun listTicketsStoryPoints(@RequestParam(name = "projectId") projectId: UUID,
                                    @RequestParam(name = "numbers", required = false) numbers: List<Int>?,
                                    @RequestParam(name = "title", required = false) title: String?,
                                    @RequestParam(name = "tags", required = false) tags: List<String>?,
                                    @RequestParam(name = "users", required = false) user: List<String>?,
                                    @RequestParam(name = "progressOne", required = false) progressOne: Float?,
                                    @RequestParam(name = "progressTwo", required = false) progressTwo: Float?,
                                    @RequestParam(name = "progressGreater", required = false) progressGreater: Boolean?,
                                    @RequestParam(name = "dueDateOne", required = false) dueDateOne: Instant?,
                                    @RequestParam(name = "dueDateTwo", required = false) dueDateTwo: Instant?,
                                    @RequestParam(name = "dueDateGreater", required = false) dueDateGreater: Boolean?,
                                    @RequestParam(name = "storyPointsOne", required = false) storyPointsOne: Int?,
                                    @RequestParam(name = "storyPointsTwo", required = false) storyPointsTwo: Int?,
                                    @RequestParam(name = "storyPointsGreater", required = false) storyPointsGreater: Boolean?,
                                    @RequestParam(name = "parent", required = false) parent: Int?): List<TicketStoryPointResultJson> {
        val tickets = ticketService.listTicketsStoryPoints(projectId, numbers, title, tags, user, progressOne, progressTwo, progressGreater, dueDateOne, dueDateTwo, dueDateGreater, storyPointsOne, storyPointsTwo, storyPointsGreater, true, parent)
        return tickets.map(::TicketStoryPointResultJson)
    }

    private fun toCreateDto(req: CreateTicketRequestJson): CreateTicket? {
        val commands = req.commands.map({
            it.toCommentCommand() ?: return null
        })
        val subtickets = req.subTickets.map({
            toCreateDto(it) ?: return null
        })

        return CreateTicket(req, subtickets, commands)
    }
}