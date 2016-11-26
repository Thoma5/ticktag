package io.ticktag.restinterface.get

import io.ticktag.ADMIN_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.get.controllers.GetController
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
        withUser(ADMIN_ID) { ->
            val result = getController.get(
                    userIds = listOf(
                            UUID.fromString("93ef43d9-20b7-461a-b960-2d1e89ba099f"),
                            UUID.fromString("660f2968-aa46-4870-bcc5-a3805366cff2"),
                            UUID.fromString("99999999-9999-9999-9999-999999999999"))
            )

            assertEquals(2, result.users.size)
            assertTrue(result.users.containsKey(UUID.fromString("93ef43d9-20b7-461a-b960-2d1e89ba099f")))
            assertTrue(result.users.containsKey(UUID.fromString("660f2968-aa46-4870-bcc5-a3805366cff2")))

            val user = result.users[UUID.fromString("93ef43d9-20b7-461a-b960-2d1e89ba099f")]!!
            assertEquals("Michael Heinzl", user.name)
        }
    }
}