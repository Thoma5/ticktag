package io.ticktag.integrationtests.service.user.services

import io.ticktag.ADMIN_ID
import io.ticktag.integrationtests.service.ServiceBaseTest
import io.ticktag.service.user.dto.UpdateUser
import io.ticktag.service.user.services.UserService
import org.junit.Assert.assertNotEquals
import org.junit.Test
import javax.inject.Inject


class UserServiceTest : ServiceBaseTest() {
    @Inject lateinit var userService: UserService


    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }

    @Test
    fun test_update_withNewPassword_shouldUpdateCurrentToken() {
        val id = ADMIN_ID

        withUser(id) { principal ->
            val tokenBefore = userService.getUser(id, principal).currentToken
            this.userService.updateUser(principal, id, UpdateUser(role = null, oldPassword = null, password = "abc", mail = null, name = null))

            val tokenNow = this.userService.getUser(id, principal).currentToken
            assertNotEquals(tokenBefore, tokenNow)
        }
    }
}
