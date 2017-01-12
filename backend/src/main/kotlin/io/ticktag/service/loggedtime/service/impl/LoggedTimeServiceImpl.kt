package io.ticktag.service.loggedtime.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.loggedtime.LoggedTimeRepository
import io.ticktag.persistence.ticket.TicketEventRepository
import io.ticktag.persistence.ticket.entity.LoggedTime
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeAdded
import io.ticktag.persistence.ticket.entity.TicketEventLoggedTimeRemoved
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.loggedtime.dto.CreateLoggedTime
import io.ticktag.service.loggedtime.dto.LoggedTimeResult
import io.ticktag.service.loggedtime.dto.UpdateLoggedTime
import io.ticktag.service.loggedtime.service.LoggedTimeService
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class LoggedTimeServiceImpl @Inject constructor(
        private val loggedTimes: LoggedTimeRepository,
        private val comments: CommentRepository,
        private val timeCategorys: TimeCategoryRepository,
        private val ticketEvents: TicketEventRepository
) : LoggedTimeService {

    @PreAuthorize(AuthExpr.USER)
    override fun getLoggedTimes(ids: List<UUID>, principal: Principal): Map<UUID, LoggedTimeResult> {
        val permittedIds = ids.filter {
            principal.hasProjectRoleForLoggedTime(it, AuthExpr.ROLE_PROJECT_OBSERVER) || principal.hasRole(AuthExpr.ROLE_GLOBAL_OBSERVER)
        }
        if (permittedIds.isEmpty()) {
            return emptyMap()
        } else {
            return loggedTimes.findByIds(ids).map(::LoggedTimeResult).associateBy { it.id }
        }
    }

    @PreAuthorize(AuthExpr.READ_COMMENT)
    override fun listLoggedTimeForComment(@P("authCommentId") commentId: UUID): List<LoggedTimeResult> {
        return loggedTimes.findAll().map(::LoggedTimeResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listLoggedTimeForProjectAndUserAndCategory(@P("authProjectId") projedId: UUID?, userId: UUID?, categoryId: UUID?): List<LoggedTimeResult> {
        return loggedTimes.findByProjectIdOrUserIdOrCategoryId(projedId, userId, categoryId).map(::LoggedTimeResult)
    }

    @PreAuthorize(AuthExpr.READ_TIME_LOG)
    override fun getLoggedTime(@P("loggedTimeId") loggedTimeId: UUID): LoggedTimeResult {
        return LoggedTimeResult(loggedTimes.findOne(loggedTimeId) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.EDIT_COMMENT)
    override fun createLoggedTime(@Valid createLoggedTime: CreateLoggedTime, @P("authCommentId") commentId: UUID): LoggedTimeResult {
        val duration = createLoggedTime.time
        val comment = comments.findOne(commentId) ?: throw NotFoundException()
        val category = timeCategorys.findOne(createLoggedTime.categoryId) ?: throw NotFoundException()
        val newLoggedTime = LoggedTime.create(duration, comment, category)
        loggedTimes.insert(newLoggedTime)
        ticketEvents.insert(TicketEventLoggedTimeAdded.create(comment.ticket, comment.user, comment, category, duration))
        return LoggedTimeResult(newLoggedTime)
    }

    @PreAuthorize(AuthExpr.EDIT_TIME_LOG)
    override fun updateLoggedTime(@Valid updateLoggedTime: UpdateLoggedTime, @P("authLoggedTimeId") loggedTimeId: UUID): LoggedTimeResult {
        val loggedTime = loggedTimes.findOne(loggedTimeId) ?: throw NotFoundException()

        val timeChanged = updateLoggedTime.time != null && loggedTime.time != updateLoggedTime.time
        val categoryChanged = updateLoggedTime.categoryId != null && loggedTime.category != timeCategorys.findOne(updateLoggedTime.categoryId)
        val canceledChanged = updateLoggedTime.canceled != null && loggedTime.canceled != updateLoggedTime.canceled
        if (timeChanged || categoryChanged || canceledChanged) {
            val category = timeCategorys.findOne(updateLoggedTime.categoryId ?: loggedTime.category.id) ?: throw NotFoundException()
            if (!loggedTime.canceled) {
                ticketEvents.insert(TicketEventLoggedTimeRemoved.create(loggedTime.comment.ticket, loggedTime.comment.user, loggedTime.comment, loggedTime.category, loggedTime.time))
            }
            if (!(updateLoggedTime.canceled ?: loggedTime.canceled)) {
                ticketEvents.insert(TicketEventLoggedTimeAdded.create(loggedTime.comment.ticket, loggedTime.comment.user, loggedTime.comment, category, updateLoggedTime.time ?: loggedTime.time))
            }
        }

        if (updateLoggedTime.time != null) {
            loggedTime.time = updateLoggedTime.time
        }

        if (updateLoggedTime.categoryId != null) {
            val category = timeCategorys.findOne(updateLoggedTime.categoryId) ?: throw NotFoundException()
            loggedTime.category = category
        }

        if (updateLoggedTime.canceled != null) {
            loggedTime.canceled = updateLoggedTime.canceled
        }

        return LoggedTimeResult(loggedTime)
    }
}