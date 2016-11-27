package io.ticktag.restinterface.get

import io.ticktag.ADMIN_ID
import io.ticktag.USER_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.get.controllers.GetController
import io.ticktag.restinterface.get.schema.GetRequestJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import javax.inject.Inject

class GetApiTest : ApiBaseTest() {
    @Inject lateinit var getController: GetController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql", "sql/WILL_BE_DELETED_SOON.sql")
    }

    @Test
    fun `should return users`() {
        withUser(ADMIN_ID) { p ->
            val result = getController.get(
                    GetRequestJson(
                            userIds = listOf(
                                    UUID.fromString("93ef43d9-20b7-461a-b960-2d1e89ba099f"),
                                    UUID.fromString("660f2968-aa46-4870-bcc5-a3805366cff2"),
                                    UUID.fromString("99999999-9999-9999-9999-999999999999")),
                            ticketIds = null),
                    principal = p
            )

            assertEquals(2, result.users.size)
            assertTrue(result.users.containsKey(UUID.fromString("93ef43d9-20b7-461a-b960-2d1e89ba099f")))
            assertTrue(result.users.containsKey(UUID.fromString("660f2968-aa46-4870-bcc5-a3805366cff2")))

            val user = result.users[UUID.fromString("93ef43d9-20b7-461a-b960-2d1e89ba099f")]!!
            assertEquals("Michael Heinzl", user.name)
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
                            ticketIds = listOf(
                                    firstId,
                                    secondId,
                                    UUID.fromString("99999999-9999-9999-9999-999999999999"))),
                    principal = p
            )

            assertEquals(0, result.users.size)
            assertEquals(1, result.tickets.size)
            assertTrue(result.tickets.containsKey(secondId))

            val ticket = result.tickets[secondId]!!
            assertEquals("Project 2 Ticket One", ticket.title)
        }
    }
}