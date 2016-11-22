package io.ticktag.service.loggedTime.service.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.LoggedTime.LoggedTimeRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.loggedTime.dto.LoggedTimeResult
import io.ticktag.service.loggedTime.service.LoggedTimeService
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class LoggedTimeServiceImpl @Inject constructor(
        private val loggedTimes:LoggedTimeRepository
) :LoggedTimeService{
    @PreAuthorize(AuthExpr.USER)
    override fun listLoggedTimeForComment(commentId: UUID): List<LoggedTimeResult>{
        return loggedTimes.findAll().map( ::LoggedTimeResult )
    }
}