package io.ticktag.service.user.services

import io.ticktag.ADMIN_ID
import io.ticktag.OBSERVER_ID
import io.ticktag.USER_ID
import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.ServiceBaseTest
import io.ticktag.service.TicktagValidationException
import io.ticktag.service.user.dto.UpdateUser
import org.junit.Assert.*
import org.junit.Test
import javax.inject.Inject


class UserServiceTest : ServiceBaseTest() {
    @Inject lateinit var userService: UserService

    @Test
    fun test_checkPassword_negative() {
        withoutUser {
            val validLogin = this.userService.checkPassword("a@a.a", "fail")
            assertNull(validLogin)
        }

    }

    @Test
    fun test_checkPassword_positive() {
        withoutUser {
            val validLogin = this.userService.checkPassword("a@a.a", "aaaa")
            assertNotNull(validLogin)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun test_checkChangePassword_negative() {
        val id = OBSERVER_ID
        withUser(id) { principal ->
            this.userService.updateUser(principal, id, UpdateUser(oldPassword = "notvalid", password = "wrong", mail = null, role = null, profilePic = null, name = null))
        }
    }


    @Test
    fun test_checkUpdate_positive_admin() {
        val id = ADMIN_ID
        val name = "name"
        val mail = "mail"
        val newPassword = "password"

        withUser(id) { principal ->
            this.userService.updateUser(principal, id, UpdateUser(oldPassword = "aaaa", password = newPassword, mail = mail, role = Role.ADMIN, profilePic = null, name = name))

            val user = this.userService.getUser(id)
            if (user == null) {
                fail()
            } else {
                assertEquals(user.name, name)
                assertEquals(user.mail, mail)
                assertEquals(user.role.name, Role.ADMIN.name)

                val newLogin = this.userService.checkPassword(mail, newPassword)
                assertNotNull(newLogin)
            }
        }
    }


    @Test
    fun test_checkUpdate_positive_own_user() {
        val id = OBSERVER_ID
        val name = "name"
        val mail = "cccc@c.c"
        val newPassword = "password"

        withUser(id) { principal ->
            this.userService.updateUser(principal, id, UpdateUser(oldPassword = "cccc", password = newPassword, mail = mail, profilePic = null, name = name, role = null))

            val user = this.userService.getUser(id)
            if (user == null) {
                fail()
            } else {
                assertEquals(user.name, name)
                assertEquals(user.mail, mail)
                assertEquals(user.role.name, Role.OBSERVER.name) //Can't change own role!

                val newLogin = this.userService.checkPassword(mail, newPassword)
                assertNotNull(newLogin)
            }
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun test_checkUpdate_withNonAdmin_shouldFail() {
        val id = OBSERVER_ID

        withUser(id) { principal ->
            this.userService.updateUser(principal, id, UpdateUser(role = Role.ADMIN, oldPassword = null, password = null, mail = null, profilePic = null, name = null))

            this.userService.getUser(id)
        }
    }

    @Test
    fun update_withNewPassword_shouldUpdateCurrentToken() {
        val id = ADMIN_ID

        withUser(id) { principal ->
            val tokenBefore = userService.getUser(id)!!.currentToken
            this.userService.updateUser(principal, id, UpdateUser(role = null, oldPassword = null, password = "abc", mail = null, profilePic = null, name = null))

            val tokenNow = this.userService.getUser(id)!!.currentToken
            assertNotEquals(tokenBefore, tokenNow)
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun test_checkUpdate_negative_other_user() {
        val ownId = USER_ID
        val otherId = OBSERVER_ID
        val name = "new Name"
        val mail = "cccc@c.c"
        val newPassword = "password"
        withUser(ownId) { principal ->
            this.userService.updateUser(principal, otherId, UpdateUser(oldPassword = "cccc", password = newPassword, mail = mail, role = Role.USER, profilePic = null, name = name))
        }
    }
}
