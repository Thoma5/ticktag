package io.ticktag.service.statistic.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.statistic.dto.TicketProgressResult
import io.ticktag.service.statistic.service.StatisticService
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Duration
import java.util.*
import javax.inject.Inject

@TicktagService
open class StatisticServiceImpl @Inject constructor(
        private val tickets: TicketRepository
) : StatisticService {

    @PreAuthorize(AuthExpr.USER) // Checked manually
    override fun getTicketProgresses(ids: Collection<UUID>, principal: Principal): Map<UUID, TicketProgressResult> {
        val permittedIds = ids.filter {
            principal.hasProjectRoleForTicket(it, AuthExpr.ROLE_PROJECT_OBSERVER) || principal.hasRole(AuthExpr.ROLE_GLOBAL_OBSERVER)
        }
        if (permittedIds.isEmpty()) {
            return emptyMap()
        }
        return tickets.findByIds(permittedIds).associateBy { it.id }.mapValues { calculateTicketProgress(it.value) }
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicketProgress(id: UUID): TicketProgressResult {
        val ticket = tickets.findOne(id) ?: throw NotFoundException()
        return calculateTicketProgress(ticket)
    }

    private fun calculateTicketProgress(ticket: Ticket): TicketProgressResult {
        val allRelevantTickets = listOf(ticket) + ticket.subTickets

        val totalEstimatedTime = allRelevantTickets.map { subTicket ->
            subTicket.currentEstimatedTime ?: subTicket.initialEstimatedTime ?: Duration.ZERO
        }.fold(Duration.ZERO, Duration::plus)

        val totalLoggedTime = allRelevantTickets.map { t ->
            val estimatedTime = estimatedTimeForTicket(t)
            var loggedTime = loggedTimeForTicket(t)
            if (loggedTime > estimatedTime) { // logged Time may not be greater than logged Time
                loggedTime = estimatedTime
            }
            loggedTime
        }.fold(Duration.ZERO, Duration::plus)

        val estimatedTime = estimatedTimeForTicket(ticket)
        var loggedTime = loggedTimeForTicket(ticket)
        if (loggedTime > estimatedTime) {
            loggedTime = estimatedTime
        }

        return TicketProgressResult(loggedTime, estimatedTime, totalLoggedTime, totalEstimatedTime)
    }

    private fun loggedTimeForTicket(ticket: Ticket): Duration {
        var duration = Duration.ZERO
        ticket.comments
                .flatMap { it.loggedTimes }
                .forEach { duration += it.time }
        return duration
    }

    private fun estimatedTimeForTicket(ticket: Ticket): Duration {
        return ticket.currentEstimatedTime ?: ticket.initialEstimatedTime ?: Duration.ZERO
    }
}