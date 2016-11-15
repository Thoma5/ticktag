package io.ticktag.service.user.services

import io.ticktag.BaseTest

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.Principal
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
    fun test_checkPassword_positive(){

        withoutUser {
            val validLogin = this.userService.checkPassword("a@a.a", "aaaa")
            assertNotNull(validLogin)
        }
    }

    @Test(expected = RuntimeException::class)
    fun test_checkChangePassword_negative() {
        withUser(UUID.fromString("00000000-0001-0000-0000-000000000001")) { principal ->
            val validLogin = this.userService.checkPassword("a@a.a", "aaaa")
            this.userService.updateUser(principal, UUID.fromString("00000000-0001-0000-0000-000000000001"), UpdateUser(oldPassword = "notvalid", password = "wrong", mail = null, role = null, profilePic = null, name = null))
        }
    }


    @Test
    fun test_checkUpdate_positiv() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val name = "name"
        val mail = "mail"
        val newPassword = "password"

        withUser(id) { principal ->
            val validLogin = this.userService.checkPassword("a@a.a", "aaaa")
            this.userService.updateUser(principal, id, UpdateUser(oldPassword = "aaaa", password = newPassword, mail = mail, role = Role.ADMIN, profilePic = null, name = name))

            val user = this.userService.getUser(id)
            if (user == null) {
                fail()
            } else {
                assertEquals(user.name, name)
                assertEquals(user.mail, mail)
                assertEquals(user.role, Role.ADMIN)

                val newLogin = this.userService.checkPassword("a@a.a", newPassword)
                assertNotNull(validLogin)
            }
        }


    }
}
