package io.ticktag.restinterface.loggTime

import io.ticktag.ADMIN_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.loggedTime.controller.LoggedTimeController
import io.ticktag.service.comment.dto.CreateLoggedTime
import io.ticktag.service.comment.dto.CreateLoggedTimeJson
import org.junit.Test
import java.time.Duration
import java.util.*
import javax.inject.Inject

class LoggTimeTest : ApiBaseTest(){
    @Inject
    lateinit var loggedTimeController: LoggedTimeController

    @Test
    fun `createLoggTime positiv`() {
        withUser(ADMIN_ID) { principal ->
            val duration = Duration.ofDays(1)
            val commentId = UUID.fromString("00000000-0004-0000-0000-000000000001")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000001")
            val req = CreateLoggedTimeJson(duration,commentId ,
                    categoryId)

            val result = loggedTimeController.createLoggedTime(req = req)
            assert(result.commentId.equals(commentId))
            assert(result.time.equals(duration))
            assert(result.categoryId.equals(categoryId))

        }
    }
}