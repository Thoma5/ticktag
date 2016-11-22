package io.ticktag.restinterface.user

import io.ticktag.ADMIN_ID
import io.ticktag.OBSERVER_ID
import io.ticktag.USER_ID
import io.ticktag.persistence.user.entity.Role
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.auth.controllers.AuthController
import io.ticktag.restinterface.auth.schema.LoginRequestJson
import io.ticktag.restinterface.user.controllers.UserController
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import io.ticktag.restinterface.user.schema.UpdateUserRequestJson
import io.ticktag.service.TicktagValidationException
import io.ticktag.service.user.dto.UpdateUser
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import javax.inject.Inject

class UserApiTest : ApiBaseTest() {
    @Inject lateinit var userController: UserController
    @Inject lateinit var authController: AuthController

    @Test(expected = AccessDeniedException::class)
    fun test_createUser_negative() {
        withUser(OBSERVER_ID) { ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", Role.USER, null)
            userController.createUser(req)
        }
    }

    @Test
    fun test_createUser_positive() {
        withUser(ADMIN_ID) { ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", Role.USER, null)
            val res = userController.createUser(req)

            val userId = res.id
            val storedUser = userController.listUsers()
                    .filter { it.id == userId }
                    .singleOrNull()

            if (storedUser == null) {
                fail("User was not saved")
            } else {
                assertThat(storedUser.mail, `is`(req.mail))
                assertThat(storedUser.name, `is`(req.name))
                assertThat(storedUser.role, `is`(req.role))
            }
        }
    }

    @Test
    fun test_updateUser_admin_positive() {
        val id = ADMIN_ID
        val name = "name"
        val mail = "mail"
        val newPassword = "password"

        withUser(id) { principal ->

            userController.updateUser(id, UpdateUserRequestJson(oldPassword = "aaaa", password = newPassword, mail = mail, role = Role.ADMIN, profilePic = null, name = name), principal)

            val user = userController.getUser(id)
            if (user == null) {
                fail()
            } else {
                assertEquals(user.name, name)
                assertEquals(user.mail, mail)
                assertEquals(user.role.name, Role.ADMIN.name)

                val result = authController.login(LoginRequestJson(mail, newPassword))
                assertNotNull(result.token)
                assertThat(result.token, `is`(not("")))
            }
        }
    }


    @Test
    fun test_updateUser_positive_own_user() {
        val id = OBSERVER_ID
        val name = "name"
        val mail = "cccc@c.c"
        val newPassword = "password"

        withUser(id) { principal ->
            userController.updateUser(id, UpdateUserRequestJson(oldPassword = "cccc", password = newPassword, mail = mail, role = null, profilePic = null, name = name), principal)

            val user = userController.getUser(id)
            if (user == null) {
                fail()
            } else {
                assertEquals(user.name, name)
                assertEquals(user.mail, mail)
                assertEquals(user.role.name, Role.OBSERVER.name) //Can't change own role!

                val result = authController.login(LoginRequestJson(mail, newPassword))
                assertNotNull(result.token)
                assertThat(result.token, `is`(not("")))
            }
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun test_checkUpdate_withNonAdmin_shouldFail() {
        val id = OBSERVER_ID

        withUser(id) { principal ->
            userController.updateUser(id, UpdateUserRequestJson(role = Role.ADMIN, oldPassword = null, password = null, mail = null, profilePic = null, name = null), principal)
        }
    }


    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun test_checkUpdate_other_user_shouldFail() {
        val ownId = USER_ID
        val otherId = OBSERVER_ID
           withUser(ownId) { principal ->
            userController.updateUser(otherId, UpdateUserRequestJson(role = Role.ADMIN, oldPassword = null, password = null, mail = null, profilePic = null, name = null), principal)
        }
    }
}