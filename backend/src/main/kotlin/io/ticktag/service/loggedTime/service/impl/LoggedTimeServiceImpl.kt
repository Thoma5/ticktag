package io.ticktag.service.loggedTime.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.LoggedTime.LoggedTimeRepository
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.ticket.entity.LoggedTime
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.comment.dto.CreateLoggedTime
import io.ticktag.service.loggedTime.dto.LoggedTimeResult
import io.ticktag.service.loggedTime.service.LoggedTimeService
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.swing.UIDefaults

@TicktagService
open class LoggedTimeServiceImpl @Inject constructor(
        private val loggedTimes:LoggedTimeRepository,
        private val comments:CommentRepository,
        private val timeCategorys: TimeCategoryRepository
) :LoggedTimeService{
    @PreAuthorize(AuthExpr.USER)
    override fun listLoggedTimeForComment(commentId: UUID): List<LoggedTimeResult>{
        return loggedTimes.findAll().map( ::LoggedTimeResult )
    }
    @PreAuthorize(AuthExpr.USER)
    override fun listLoggedTimeForProjectAndUserAndCategory(projedId: UUID?, userId: UUID?, categoryId: UUID?): List<LoggedTimeResult>{
        return loggedTimes.findByProjectIdOrUserIdOrCategoryId(projedId,userId,categoryId).map(::LoggedTimeResult)
    }
    @PreAuthorize(AuthExpr.USER)
    override fun createLoggedTime(createLoggedTime: CreateLoggedTime): LoggedTimeResult {
        val duration = createLoggedTime.time
        val comment = comments.findOne(createLoggedTime.commentId)?:throw NotFoundException()
        val category = timeCategorys.findOne(createLoggedTime.categoryId)?:throw NotFoundException()
        val newLoggedTime = LoggedTime.create(duration,comment,category)
        loggedTimes.insert(newLoggedTime)
        return LoggedTimeResult(newLoggedTime)
    }
}