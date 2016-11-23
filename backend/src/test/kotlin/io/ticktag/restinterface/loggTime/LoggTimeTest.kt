package io.ticktag.restinterface.loggTime

import io.ticktag.ADMIN_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.loggedTime.controller.LoggedTimeController
import io.ticktag.restinterface.loggedTime.schema.UpdateLoggedTimeJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.comment.dto.CreateLoggedTimeJson
import org.junit.Test
import java.time.Duration
import java.util.*
import javax.inject.Inject
import org.springframework.security.access.AccessDeniedException

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

    @Test
    fun `updateLoggTime positiv`() {
        withUser(ADMIN_ID) { principal ->
            val duration = Duration.ofDays(2)
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000002")
            val loggTimeId = UUID.fromString("00000000-0008-0000-0000-000000000001")
            val req = UpdateLoggedTimeJson(duration,categoryId)
            val result = loggedTimeController.updateLoggedTime(req = req,loggedTimeId = loggTimeId)
            assert(result.time.equals(duration))
            assert(result.categoryId.equals(categoryId))
        }
    }

    @Test
    fun `listLoggTime positiv`() {
        withUser(ADMIN_ID) { principal ->
            val commentId = UUID.fromString("00000000-0003-0000-0000-000000000006")
            val result = loggedTimeController.getLoggedTimesForComment(commentId)
        }
    }

    @Test
    fun `listLoggTime With ProjectId UserId and CategoryId positiv`() {
        withUser(ADMIN_ID) { principal ->
            val userId = UUID.fromString("660f2968-aa46-4870-bcc5-a3805366cff2")
            val projectId = UUID.fromString("00000000-0002-0000-0000-000000000001")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000001")
            val result = loggedTimeController.getLoggedTimesForProjectAndUserAndCategory(projectId = projectId,
                    userId = userId, categoryId = categoryId)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `deleteLoggTime positiv`() {
        withUser(ADMIN_ID) { principal ->
            val loggTimeId = UUID.fromString("00000000-0008-0000-0000-000000000001")
             loggedTimeController.deleteLoggedTime(loggTimeId)
            loggedTimeController.getLoggedTimesForId(loggTimeId)
        }
    }


    @Test(expected = AccessDeniedException::class)
    fun `createLoggTime negativ permission`() {
        withoutUser { ->
            val duration = Duration.ofDays(1)
            val commentId = UUID.fromString("00000000-0004-0000-0000-000000000001")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000001")
            val req = CreateLoggedTimeJson(duration,commentId ,
                    categoryId)

            val result = loggedTimeController.createLoggedTime(req = req)

        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `updateLoggTime  negativ permission`() {
        withoutUser {->
            val duration = Duration.ofDays(2)
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000002")
            val loggTimeId = UUID.fromString("00000000-0008-0000-0000-000000000001")
            val req = UpdateLoggedTimeJson(duration,categoryId)
            val result = loggedTimeController.updateLoggedTime(req = req,loggedTimeId = loggTimeId)

        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `listLoggTime  negativ permission`() {
        withoutUser {->
        val commentId = UUID.fromString("00000000-0003-0000-0000-000000000006")
            val result = loggedTimeController.getLoggedTimesForComment(commentId)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `listLoggTime With ProjectId UserId and CategoryId  negativ permission`() {
        withoutUser {->
        val userId = UUID.fromString("660f2968-aa46-4870-bcc5-a3805366cff2")
            val projectId = UUID.fromString("00000000-0002-0000-0000-000000000001")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000001")
            val result = loggedTimeController.getLoggedTimesForProjectAndUserAndCategory(projectId = projectId,
                    userId = userId, categoryId = categoryId)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `deleteLoggTime  negativ permission`() {
        withoutUser {->
            val loggTimeId = UUID.fromString("00000000-0008-0000-0000-000000000001")
            loggedTimeController.deleteLoggedTime(loggTimeId)
        }
    }

}