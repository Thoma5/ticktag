package io.ticktag.service.user.services

import io.ticktag.BaseTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import javax.inject.Inject


class UserServiceTest : BaseTest() {
    @Inject lateinit var userService: UserService

    @Test
    fun test_checkPassword_negative() {
        withoutUser {
            val validLogin = this.userService.checkPassword("a@a.a", "fail")
            assertNull(validLogin)
        }
    }

    @Test
    fun test_checkPassword_positive(){
        withoutUser {
            val validLogin = this.userService.checkPassword("a@a.a", "aaaa")
            assertNotNull(validLogin)
        }
    }
}
