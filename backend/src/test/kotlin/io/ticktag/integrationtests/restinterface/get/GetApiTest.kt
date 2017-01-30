package io.ticktag.integrationtests.restinterface.get

import io.ticktag.ADMIN_ID
import io.ticktag.USER_ID
import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.restinterface.get.controllers.GetController
import io.ticktag.restinterface.get.schema.GetRequestJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.util.*
import javax.inject.Inject

class GetApiTest : ApiBaseTest() {
    @Inject lateinit var getController: GetController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }

    @Test
    fun `should return users`() {
        withUser(ADMIN_ID) { p ->
            val result = getController.get(
                    GetRequestJson(
                            userIds = listOf(
                                    UUID.fromString("00000000-0001-0000-0000-000000000101"),
                                    UUID.fromString("00000000-0001-0000-0000-000000000102"),
                                    UUID.fromString("00000000-0001-0000-0000-000000000103")),
                            ticketIds = null,
                            loggedTimeIds = null,
                            ticketIdsForStatistic = null),
                    principal = p
            )

            assertEquals(3, result.users.size)
            assertEquals(0, result.tickets.size)
            assertEquals(0, result.ticketStatistics.size)
            assertTrue(result.users.containsKey(UUID.fromString("00000000-0001-0000-0000-000000000101")))
            assertTrue(result.users.containsKey(UUID.fromString("00000000-0001-0000-0000-000000000102")))

            val user = result.users[UUID.fromString("00000000-0001-0000-0000-000000000101")]!!
            assertEquals("Admiral Admin", user.name)
        }
    }

    @Test
    fun `should return correct tickets and skip missing or forbidden`() {
        withUser(USER_ID) { p ->
            val firstId = UUID.fromString("00000000-0003-0000-0000-000000000104")
            val secondId = UUID.fromString("00000000-0003-0000-0000-000000000102")
            val result = getController.get(
                    GetRequestJson(
                            userIds = null,
                            loggedTimeIds = null,
                            ticketIds = listOf(
                                    firstId,
                                    secondId,
                                    UUID.fromString("00000000-0001-0000-0000-000000000103")),
                            ticketIdsForStatistic = null),
                    principal = p
            )

            assertEquals(0, result.users.size)
            assertEquals(1, result.tickets.size)
            assertEquals(0, result.ticketStatistics.size)
            assertTrue(result.tickets.containsKey(secondId))

            val ticket = result.tickets[secondId]!!
            assertEquals("Project 2 Ticket One", ticket.title)
        }
    }

    @Test
    fun `should return correct statistics and skip missing or forbidden`() {
        withUser(USER_ID) { p ->
            val firstId = UUID.fromString("00000000-0003-0000-0000-000000000104")
            val secondId = UUID.fromString("00000000-0003-0000-0000-000000000102")
            val result = getController.get(
                    GetRequestJson(
                            userIds = null,
                            ticketIds = null,
                            loggedTimeIds = null,
                            ticketIdsForStatistic = listOf(
                                    firstId,
                                    secondId,
                                    UUID.fromString("00000000-0001-0000-0000-000000000103"))),
                    principal = p
            )

            assertEquals(0, result.users.size)
            assertEquals(0, result.tickets.size)
            assertEquals(1, result.ticketStatistics.size)
            assertTrue(result.ticketStatistics.containsKey(secondId))

            val stat = result.ticketStatistics[secondId]!!
            assertEquals(Duration.ofNanos(25), stat.totalCurrentEstimatedTime)
            assertEquals(Duration.ZERO, stat.totalLoggedTime)
        }
    }
}