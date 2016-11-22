package io.ticktag.restinterface.loggedTime.controller

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.loggedTime.schema.LoggedTimeResultJson
import io.ticktag.service.loggedTime.service.LoggedTimeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*
import javax.inject.Inject


@TicktagRestInterface
@RequestMapping("/logged_time")
@Api(tags = arrayOf("loggedTime"), description = "loggedTime management")
open class LoggedTimeController @Inject constructor(
        private val loggedTimeService: LoggedTimeService
) {

    @GetMapping()
    open fun getLoggedTimes(
            @RequestParam commentId: UUID): List<LoggedTimeResultJson> {
        val loggedTime = loggedTimeService.listLoggedTimeForComment(commentId)
        return loggedTime.map(::LoggedTimeResultJson)
    }
}