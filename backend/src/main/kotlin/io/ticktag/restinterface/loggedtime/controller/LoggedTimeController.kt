package io.ticktag.restinterface.loggedtime.controller

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.loggedtime.schema.CreateLoggedTimeJson
import io.ticktag.restinterface.loggedtime.schema.LoggedTimeResultJson
import io.ticktag.restinterface.loggedtime.schema.UpdateLoggedTimeJson
import io.ticktag.service.loggedtime.dto.CreateLoggedTime
import io.ticktag.service.loggedtime.dto.UpdateLoggedTime
import io.ticktag.service.loggedtime.service.LoggedTimeService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject


@TicktagRestInterface
@RequestMapping("/loggedtime")
@Api(tags = arrayOf("loggedtime"), description = "logged time management")
open class LoggedTimeController @Inject constructor(
        private val loggedTimeService: LoggedTimeService
) {

    @GetMapping(value = "/{id}")
    open fun getLoggedTimesForId(
            @RequestParam id: UUID): LoggedTimeResultJson {
        val loggedTime = loggedTimeService.getLoggedTime(id)
        return LoggedTimeResultJson(loggedTime)
    }

    @GetMapping
    open fun getLoggedTimesForComment(
            @RequestParam commentId: UUID): List<LoggedTimeResultJson> {
        val loggedTime = loggedTimeService.listLoggedTimeForComment(commentId)
        return loggedTime.map(::LoggedTimeResultJson)
    }

    @GetMapping(value = "/search")
    open fun getLoggedTimesForProjectAndUserAndCategory(
            @RequestParam(required = false) projectId: UUID?,
            @RequestParam(required = false) userId: UUID?,
            @RequestParam(required = false) categoryId: UUID?): List<LoggedTimeResultJson> {
        val loggedTime = loggedTimeService.listLoggedTimeForProjectAndUserAndCategory(projectId, userId, categoryId)
        return loggedTime.map(::LoggedTimeResultJson)
    }

    @PostMapping
    open fun createLoggedTime(@RequestBody req: CreateLoggedTimeJson): LoggedTimeResultJson {
        val newLoggedTime = loggedTimeService.createLoggedTime(CreateLoggedTime(req.time, req.categoryId), req.commentId)
        return LoggedTimeResultJson(newLoggedTime)
    }

    @PutMapping(value = "/{id}")
    open fun updateLoggedTime(@RequestBody req: UpdateLoggedTimeJson,
                              @RequestParam id: UUID): LoggedTimeResultJson {
        val newLoggedTime = loggedTimeService.updateLoggedTime(UpdateLoggedTime(req.time, req.categoryId, req.canceled), id)
        return LoggedTimeResultJson(newLoggedTime)
    }
}