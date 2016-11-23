package io.ticktag.service.loggedTime.service

import io.ticktag.service.comment.dto.CreateLoggedTime
import io.ticktag.service.loggedTime.dto.LoggedTimeResult
import io.ticktag.service.loggedTime.dto.UpdateLoggedTime
import java.util.*


interface LoggedTimeService {
    fun listLoggedTimeForComment(commentId: UUID): List<LoggedTimeResult>
    fun listLoggedTimeForProjectAndUserAndCategory(projedId: UUID?, userId: UUID?, categoryId: UUID?): List<LoggedTimeResult>
    fun createLoggedTime(createLoggedTime: CreateLoggedTime, commentId: UUID) : LoggedTimeResult
    fun updateLoggedTime(updateLoggedTime: UpdateLoggedTime,loggedTimeId:UUID) : LoggedTimeResult
    fun deleteLoggedTime(loggedTimeId: UUID)
    fun getLoggedTime(loggedTimeId: UUID) :LoggedTimeResult
}