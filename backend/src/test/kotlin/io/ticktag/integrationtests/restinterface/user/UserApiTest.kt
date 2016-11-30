package io.ticktag.restinterface.user

import io.ticktag.ADMIN_ID
import io.ticktag.OBSERVER_ID
import io.ticktag.OBSERVER_PASSWORD
import io.ticktag.USER_ID
import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.persistence.user.entity.Role
import io.ticktag.restinterface.auth.controllers.AuthController
import io.ticktag.restinterface.auth.schema.LoginRequestJson
import io.ticktag.restinterface.user.controllers.UserController
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import io.ticktag.restinterface.user.schema.UpdateUserRequestJson
import io.ticktag.restinterface.user.schema.UserSort
import io.ticktag.service.TicktagValidationException
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.inject.Inject

class UserApiTest : ApiBaseTest() {
    @Inject lateinit var userController: UserController
    @Inject lateinit var authController: AuthController


    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql", "sql/testUserSamples.sql")
    }


    @Test(expected = AccessDeniedException::class)
    fun test_createUser_negative() {
        withUser(OBSERVER_ID) { ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", "unique_test_user", Role.USER, null)
            userController.createUser(req)
        }
    }

    @Test
    fun test_createUser_positive() {
        withUser(ADMIN_ID) { ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", "unique_test_user", Role.USER, null)
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

            assertEquals(user.name, name)
            assertEquals(user.mail, mail)
            assertEquals(user.role.name, Role.ADMIN.name)

            val result = authController.login(LoginRequestJson(mail, newPassword))
            assertNotNull(result.token)
            assertThat(result.token, `is`(not("")))
        }
    }


    @Test
    fun test_updateUser_positive_own_user() {
        val id = OBSERVER_ID
        val name = "name"
        val mail = "cccc@c.c"
        val newPassword = "password"

        withUser(id) { principal ->
            userController.updateUser(id, UpdateUserRequestJson(oldPassword = OBSERVER_PASSWORD, password = newPassword, mail = mail, role = null, profilePic = null, name = name), principal)

            val user = userController.getUser(id)

            assertEquals(user.name, name)
            assertEquals(user.mail, mail)
            assertEquals(user.role.name, Role.OBSERVER.name) //Can't change own role!

            val result = authController.login(LoginRequestJson(mail, newPassword))
            assertNotNull(result.token)
            assertThat(result.token, `is`(not("")))
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

    @Test
    fun `listUsersFuzzy should find two users`() {
        withUser(ADMIN_ID) { ->
            val users = userController.listUsersFuzzy(UUID.fromString("00000000-0002-0000-0000-000000000001"), "user", listOf(UserSort.NAME_ASC))

            assertEquals(2, users.size)
            assertEquals("Berta Berta", users[0].name)
            assertEquals("Mr. A", users[1].name)
        }
    }
}