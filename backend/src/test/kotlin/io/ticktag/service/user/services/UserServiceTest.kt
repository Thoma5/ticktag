package io.ticktag.service.user.services

import io.ticktag.ADMIN_ID
import io.ticktag.service.ServiceBaseTest
import io.ticktag.service.user.dto.UpdateUser
import org.junit.Assert.assertNotEquals
import org.junit.Test
import javax.inject.Inject


class UserServiceTest : ServiceBaseTest() {
    @Inject lateinit var userService: UserService

    @Test
    fun test_update_withNewPassword_shouldUpdateCurrentToken() {
        val id = ADMIN_ID

        withUser(id) { principal ->
            val tokenBefore = userService.getUser(id)!!.currentToken
            this.userService.updateUser(principal, id, UpdateUser(role = null, oldPassword = null, password = "abc", mail = null, profilePic = null, name = null))

            val tokenNow = this.userService.getUser(id)!!.currentToken
            assertNotEquals(tokenBefore, tokenNow)
        }
    }
}
