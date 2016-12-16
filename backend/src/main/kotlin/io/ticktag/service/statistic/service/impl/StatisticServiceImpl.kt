package io.ticktag.service.statistic.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.ticket.TicketRepository
import io.ticktag.persistence.ticket.entity.Progress
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
        return calculateTicketProgresses(tickets.findByIds(permittedIds)).mapKeys { it.key.id }
    }

    @PreAuthorize(AuthExpr.READ_TICKET)
    override fun getTicketProgress(id: UUID): TicketProgressResult {
        val ticket = tickets.findOne(id) ?: throw NotFoundException()
        return calculateTicketProgresses(listOf(ticket)).values.single()
    }

    private fun calculateTicketProgresses(ts: List<Ticket>): Map<Ticket, TicketProgressResult> {
        val allProgresses = tickets.findProgressesByTicketIds(ts.map(Ticket::id))
        return ts.associateBy({ it }, { calculateTicketProgress(it, allProgresses) })
    }

    private fun calculateTicketProgress(
            ticket: Ticket,
            allProgresses: Map<UUID, Progress>
    ): TicketProgressResult {
        val progress = allProgresses[ticket.id]!!
        val totalEstimatedTime = estimatedTime(progress.totalInitialEstimatedTime, progress.currentEstimatedTime)
        val estimatedTime = estimatedTime(ticket.initialEstimatedTime, ticket.currentEstimatedTime)

        return TicketProgressResult(progress.loggedTime, estimatedTime, progress.totalLoggedTime, totalEstimatedTime)
    }

    private fun estimatedTime(initial: Duration?, current: Duration?): Duration {
        return current ?: initial ?: Duration.ZERO
    }
}