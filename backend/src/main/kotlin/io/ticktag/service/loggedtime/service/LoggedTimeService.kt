package io.ticktag.service.loggedtime.service

import io.ticktag.service.Principal
import io.ticktag.service.loggedtime.dto.CreateLoggedTime
import io.ticktag.service.loggedtime.dto.LoggedTimeResult
import io.ticktag.service.loggedtime.dto.UpdateLoggedTime
import java.util.*


interface LoggedTimeService {
    fun listLoggedTimeForComment(commentId: UUID): List<LoggedTimeResult>
    fun listLoggedTimeForProjectAndUserAndCategory(projedId: UUID?, userId: UUID?, categoryId: UUID?): List<LoggedTimeResult>
    fun createLoggedTime(createLoggedTime: CreateLoggedTime, commentId: UUID): LoggedTimeResult
    fun updateLoggedTime(updateLoggedTime: UpdateLoggedTime, loggedTimeId: UUID): LoggedTimeResult
    fun getLoggedTime(loggedTimeId: UUID): LoggedTimeResult
    fun getLoggedTimes(ids: List<UUID>, principal: Principal): Map<UUID, LoggedTimeResult>
}