package io.ticktag.service.statistic.service

import io.ticktag.service.statistic.dto.TicketProgressResult
import java.util.*


interface StatisticService {
    fun getTicketProgress(id: UUID): TicketProgressResult
}