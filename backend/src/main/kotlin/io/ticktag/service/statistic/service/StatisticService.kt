package io.ticktag.service.statistic.service

import io.ticktag.service.Principal
import io.ticktag.service.statistic.dto.TicketProgressResult
import java.util.*


interface StatisticService {
    /**
     * Returns all Progresses of each submitted TicketId
     */
    fun getTicketProgresses(ids: Collection<UUID>, principal: Principal): Map<UUID, TicketProgressResult>

    /**
     * Get Progress of one ticket
     */
    fun getTicketProgress(id: UUID): TicketProgressResult
}