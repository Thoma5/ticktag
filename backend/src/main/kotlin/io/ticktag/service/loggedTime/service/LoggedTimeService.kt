package io.ticktag.service.loggedTime.service

import io.ticktag.service.comment.dto.CreateLoggedTime
import io.ticktag.service.loggedTime.dto.LoggedTimeResult
import java.util.*

/**
 * Created by stefandraskovits on 22/11/2016.
 */
interface LoggedTimeService {
    fun listLoggedTimeForComment(commentId: UUID): List<LoggedTimeResult>
    fun listLoggedTimeForProjectAndUserAndCategory(projedId: UUID?, userId: UUID?, categoryId: UUID?): List<LoggedTimeResult>
    fun createLoggedTime(createLoggedTime: CreateLoggedTime) : LoggedTimeResult
}