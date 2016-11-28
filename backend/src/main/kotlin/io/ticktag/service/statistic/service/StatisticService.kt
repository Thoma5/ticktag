package io.ticktag.service.statistic.service

import io.ticktag.service.Principal
import io.ticktag.service.statistic.dto.TicketProgressResult
import java.util.*


interface StatisticService {
    fun getTicketProgresses(ids: Collection<UUID>, principal: Principal): Map<UUID, TicketProgressResult>
    fun getTicketProgress(id: UUID): TicketProgressResult
}