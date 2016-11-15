package io.ticktag.service.user.services

import io.ticktag.BaseTest

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.Principal
import io.ticktag.service.TicktagValidationException
import io.ticktag.service.ValidationError
import io.ticktag.service.user.dto.UpdateUser
import org.junit.Assert.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

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
    fun test_checkPassword_positive() {

        withoutUser {
            val validLogin = this.userService.checkPassword("a@a.a", "aaaa")
            assertNotNull(validLogin)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun test_checkChangePassword_negative() {
        withUser(UUID.fromString("00000000-0001-0000-0000-000000000003")) { principal ->
            this.userService.updateUser(principal, UUID.fromString("00000000-0001-0000-0000-000000000003"), UpdateUser(oldPassword = "notvalid", password = "wrong", mail = null, role = null, profilePic = null, name = null))
        }
    }


    @Test
    fun test_checkUpdate_positive_admin() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
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
        val id = UUID.fromString("00000000-0001-0000-0000-000000000003")
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
    fun test_checkUpdate_withNonAdmin_shoudlFail() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000003")

        withUser(id) { principal ->
            this.userService.updateUser(principal, id, UpdateUser(role = Role.ADMIN, oldPassword = null,password = null, mail = null,profilePic = null,name = null))

            val user = this.userService.getUser(id)
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun test_checkUpdate_negative_other_user() {
        val ownId = UUID.fromString("00000000-0001-0000-0000-000000000002")
        val otherId = UUID.fromString("00000000-0001-0000-0000-000000000003")
        val name = "new Name"
        val mail = "cccc@c.c"
        val newPassword = "password"
        withUser(ownId) { principal ->
            this.userService.updateUser(principal, otherId, UpdateUser(oldPassword = "cccc", password = newPassword, mail = mail, role = Role.USER, profilePic = null, name = name))
        }
    }
}
