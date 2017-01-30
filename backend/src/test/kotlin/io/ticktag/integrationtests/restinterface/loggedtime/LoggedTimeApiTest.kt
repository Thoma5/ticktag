package io.ticktag.integrationtests.restinterface.loggedtime

import io.ticktag.ADMIN_ID
import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.restinterface.loggedtime.controller.LoggedTimeController
import io.ticktag.restinterface.loggedtime.schema.CreateLoggedTimeJson
import io.ticktag.restinterface.loggedtime.schema.UpdateLoggedTimeJson
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.time.Duration
import java.util.*
import javax.inject.Inject
import kotlin.test.assertEquals

class LoggedTimeApiTest : ApiBaseTest() {
    @Inject
    lateinit var loggedTimeController: LoggedTimeController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }

    @Test
    fun `createLoggTime positiv`() {
        withUser(ADMIN_ID) { principal ->
            val duration = Duration.ofDays(1)
            val commentId = UUID.fromString("00000000-0004-0000-0000-000000000101")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000101")
            val req = CreateLoggedTimeJson(duration, commentId,
                    categoryId)

            val result = loggedTimeController.createLoggedTime(req = req)
            assertEquals(result.commentId,(commentId))
            assertEquals(result.time,(duration))
            assertEquals(result.categoryId,(categoryId))

        }
    }

    @Test
    fun `updateLoggTime positiv`() {
        withUser(ADMIN_ID) { principal ->
            val duration = Duration.ofDays(2)
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000102")
            val loggTimeId = UUID.fromString("00000000-0008-0000-0000-000000000101")
            val req = UpdateLoggedTimeJson(duration, categoryId, false)
            val result = loggedTimeController.updateLoggedTime(req = req, id = loggTimeId)
            assertEquals(result.time,(duration))
            assertEquals(result.categoryId,(categoryId))
        }
    }

    @Test
    fun `updateLoggTime canceled`() {
        withUser(ADMIN_ID) { principal ->
            val loggTimeId = UUID.fromString("00000000-0008-0000-0000-000000000101")
            val req = UpdateLoggedTimeJson(null, null, true)
            val result = loggedTimeController.updateLoggedTime(req = req, id = loggTimeId)
            assertEquals(true, result.canceled)
        }
    }

    @Test
    fun `listLoggTime positiv`() {
        withUser(ADMIN_ID) { principal ->
            val commentId = UUID.fromString("00000000-0003-0000-0000-000000000006")
            loggedTimeController.getLoggedTimesForComment(commentId)
        }
    }

    @Test
    fun `listLoggTime With ProjectId UserId and CategoryId positiv`() {
        withUser(ADMIN_ID) { principal ->
            val userId = UUID.fromString("00000000-0001-0000-0000-000000000103")
            val projectId = UUID.fromString("00000000-0002-0000-0000-000000000101")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000101")
            loggedTimeController.getLoggedTimesForProjectAndUserAndCategory(projectId = projectId,
                    userId = userId, categoryId = categoryId)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `createLoggTime negativ permission`() {
        withoutUser { ->
            val duration = Duration.ofDays(1)
            val commentId = UUID.fromString("00000000-0004-0000-0000-000000000101")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000101")
            val req = CreateLoggedTimeJson(duration, commentId,
                    categoryId)

            loggedTimeController.createLoggedTime(req = req)

        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `updateLoggTime  negativ permission`() {
        withoutUser { ->
            val duration = Duration.ofDays(2)
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000102")
            val loggTimeId = UUID.fromString("00000000-0008-0000-0000-000000000101")
            val req = UpdateLoggedTimeJson(duration, categoryId, false)
            loggedTimeController.updateLoggedTime(req = req, id = loggTimeId)

        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `listLoggTime  negativ permission`() {
        withoutUser { ->
            val commentId = UUID.fromString("00000000-0003-0000-0000-000000000006")
            loggedTimeController.getLoggedTimesForComment(commentId)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `listLoggTime With ProjectId UserId and CategoryId  negativ permission`() {
        withoutUser { ->
            val userId = UUID.fromString("00000000-0001-0000-0000-000000000103")
            val projectId = UUID.fromString("00000000-0002-0000-0000-000000000101")
            val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000101")
            loggedTimeController.getLoggedTimesForProjectAndUserAndCategory(projectId = projectId,
                    userId = userId, categoryId = categoryId)
        }
    }
}