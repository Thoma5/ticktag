package io.ticktag.service.loggedtime.service

import io.ticktag.service.Principal
import io.ticktag.service.loggedtime.dto.CreateLoggedTime
import io.ticktag.service.loggedtime.dto.LoggedTimeResult
import io.ticktag.service.loggedtime.dto.UpdateLoggedTime
import java.util.*


interface LoggedTimeService {
    /**
     * List all loggedTime elements of a comment
     */
    fun listLoggedTimeForComment(commentId: UUID): List<LoggedTimeResult>

    /**
     * List all logged Time Objects for a project and use and a category(Time Category)
     */
    fun listLoggedTimeForProjectAndUserAndCategory(projedId: UUID?, userId: UUID?, categoryId: UUID?): List<LoggedTimeResult>

    /**
     * create a Logged Time Object for a comment.
     * Logged Time have to be always related to a Comment.
     */
    fun createLoggedTime(createLoggedTime: CreateLoggedTime, commentId: UUID): LoggedTimeResult

    /**
     * Update a Logged Time Object
     */
    fun updateLoggedTime(updateLoggedTime: UpdateLoggedTime, loggedTimeId: UUID): LoggedTimeResult

    /**
     * Get a specific LoggedTime Object
     */
    fun getLoggedTime(loggedTimeId: UUID): LoggedTimeResult

    /**
     * Get Logged Time Objects with the given Ids.
     * This Function will be used to resolve nested Objects.
     */
    fun getLoggedTimes(ids: List<UUID>, principal: Principal): Map<UUID, LoggedTimeResult>
}