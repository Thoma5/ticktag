package io.ticktag.service.ticket.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.entity.TicketRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.ticket.dto.TicketResult
import io.ticktag.service.ticket.service.TicketService
import org.springframework.security.access.prepost.PreAuthorize
import javax.inject.Inject

@TicktagService
open class TicketServiceImpl @Inject constructor(
        private val tickets: TicketRepository

) :TicketService{

    @PreAuthorize(AuthExpr.USER) // TODO should probably be more granular
    override fun listTickets(): List<TicketResult> {
        return tickets.findAll().map(::TicketResult)
    }
}