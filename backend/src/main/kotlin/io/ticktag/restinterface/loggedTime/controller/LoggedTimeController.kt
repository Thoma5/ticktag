package io.ticktag.restinterface.loggedTime.controller

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.loggedTime.schema.LoggedTimeResultJson
import io.ticktag.restinterface.loggedTime.schema.UpdateLoggedTimeJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.loggedTime.dto.CreateLoggedTime
import io.ticktag.service.comment.dto.CreateLoggedTimeJson
import io.ticktag.service.loggedTime.dto.UpdateLoggedTime
import io.ticktag.service.loggedTime.service.LoggedTimeService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject


@TicktagRestInterface
@RequestMapping("/logged_time")
@Api(tags = arrayOf("loggedTime"), description = "loggedTime management")
open class LoggedTimeController @Inject constructor(
        private val loggedTimeService: LoggedTimeService
) {

    @GetMapping(value = "/{id}")
    open fun getLoggedTimesForId(
            @RequestParam loggTimeId: UUID): LoggedTimeResultJson {
        val loggedTime = loggedTimeService.getLoggedTime(loggTimeId)
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
        val newLoggedTime = loggedTimeService.createLoggedTime(CreateLoggedTime(req.time, req.commentId, req.categoryId), req.commentId ?: throw NotFoundException())
        return LoggedTimeResultJson(newLoggedTime)
    }

    @PutMapping(value = "/{id}")
    open fun updateLoggedTime(@RequestBody req: UpdateLoggedTimeJson,
                              @RequestParam loggedTimeId: UUID): LoggedTimeResultJson {
        val newLoggedTime = loggedTimeService.updateLoggedTime(UpdateLoggedTime(req.time, req.categoryId), loggedTimeId)
        return LoggedTimeResultJson(newLoggedTime)
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteLoggedTime(@RequestParam loggedTimeId: UUID) {
        loggedTimeService.deleteLoggedTime(loggedTimeId)
    }
}