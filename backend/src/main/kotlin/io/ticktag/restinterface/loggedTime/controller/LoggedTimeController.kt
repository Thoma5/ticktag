package io.ticktag.restinterface.loggedTime.controller

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.loggedTime.schema.LoggedTimeResultJson
import io.ticktag.service.comment.dto.CreateLoggedTime
import io.ticktag.service.comment.dto.CreateLoggedTimeJson
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
    open fun getLoggedTimesForComment(
            @RequestParam commentId: UUID): List<LoggedTimeResultJson> {
        val loggedTime = loggedTimeService.listLoggedTimeForComment(commentId)
        return loggedTime.map(::LoggedTimeResultJson)
    }

    @GetMapping()
    open fun getLoggedTimesForProjectAndUserAndCategory(
            @RequestParam(required = false) projectId: UUID?,
            @RequestParam(required = false) userId: UUID?,
            @RequestParam(required = false) categoryId: UUID?): List<LoggedTimeResultJson> {
        val loggedTime = loggedTimeService.listLoggedTimeForProjectAndUserAndCategory(projectId,userId,categoryId)
        return loggedTime.map(::LoggedTimeResultJson)
    }

    @PostMapping
    open fun createLoggedTime(@RequestBody req: CreateLoggedTimeJson):LoggedTimeResultJson{
        val newLoggedTime = loggedTimeService.createLoggedTime(CreateLoggedTime(req.time,req.commentId,req.categoryId))
        return LoggedTimeResultJson(newLoggedTime)
    }
}