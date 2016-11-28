package io.ticktag.restinterface.statistic.controller

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.statistic.schema.TicketProgressResultJson
import io.ticktag.service.statistic.service.StatisticService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import javax.inject.Inject


@TicktagRestInterface
@RequestMapping("/statistic")
@Api(tags = arrayOf("statistic"), description = "statistic management")
open class StatisticController @Inject constructor(
        private val statisticsService: StatisticService
) {

    @GetMapping(value = "/progress/{id}")
    open fun getTicketProgress(@PathVariable(name = "id") id: UUID): TicketProgressResultJson {
        return TicketProgressResultJson(statisticsService.getTicketProgress(id))
    }
}