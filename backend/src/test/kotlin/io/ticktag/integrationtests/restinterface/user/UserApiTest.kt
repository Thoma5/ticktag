package io.ticktag.integrationtests.restinterface.user

import io.ticktag.*
import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.persistence.user.entity.Role
import io.ticktag.restinterface.auth.controllers.AuthController
import io.ticktag.restinterface.auth.schema.LoginRequestJson
import io.ticktag.restinterface.user.controllers.UserController
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import io.ticktag.restinterface.user.schema.UpdateUserRequestJson
import io.ticktag.restinterface.user.schema.UserSort
import io.ticktag.service.TicktagValidationException
import org.apache.commons.codec.binary.Base64
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.access.AccessDeniedException
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject
import kotlin.test.assertFailsWith

class UserApiTest : ApiBaseTest() {
    @Inject lateinit var userController: UserController
    @Inject lateinit var authController: AuthController
    @Inject lateinit var timeMachine: TimeMachine


    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql", "sql/testUserSamples.sql")
    }


    @Test(expected = AccessDeniedException::class)
    fun test_createUser_negative() {
        withUser(OBSERVER_ID) { p ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", "unique_test_user", Role.USER)
            userController.createUser(req, p)
        }
    }

    @Test
    fun test_createUser_positive() {
        withUser(ADMIN_ID) { p ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", "unique_test_user", Role.USER)
            val res = userController.createUser(req, p)

            val userId = res.id
            val storedUser = userController.listUsers(0,100, listOf(UserSort.NAME_ASC),"",null, false, p).content
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

            userController.updateUser(id, UpdateUserRequestJson(oldPassword = "aaaa", password = newPassword, mail = mail, role = Role.ADMIN, disabled = false, name = name), principal)

            val user = userController.getUser(id, principal)

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
            userController.updateUser(id, UpdateUserRequestJson(oldPassword = OBSERVER_PASSWORD, password = newPassword, mail = mail, role = null, disabled = true, name = name), principal)

            val user = userController.getUser(id, principal)

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
            userController.updateUser(id, UpdateUserRequestJson(role = Role.ADMIN, oldPassword = null, password = null, mail = null, name = null, disabled = false), principal)
        }
    }


    @Test(expected = AccessDeniedException::class)
    fun test_checkUpdate_other_user_shouldFail() {
        val ownId = USER_ID
        val otherId = OBSERVER_ID
        withUser(ownId) { principal ->
            userController.updateUser(otherId, UpdateUserRequestJson(role = Role.ADMIN, oldPassword = null, password = null, mail = null, name = null, disabled = true), principal)
        }
    }

    @Test
    fun `listUsersFuzzy should find two users`() {
        withUser(ADMIN_ID) { p ->
            val users = userController.listUsersFuzzy(UUID.fromString("00000000-0002-0000-0000-000000000001"), "user", listOf(UserSort.NAME_ASC), p)

            assertEquals(2, users.size)
            assertEquals("Berta Berta", users[0].name)
            assertEquals("Mr. A", users[1].name)
        }
    }

    @Test
    fun `getUser with other user should hide email`() {
        val newUser = withUser(ADMIN_ID) { p ->
            userController.createUser(CreateUserRequestJson("newuser@example.com", "newuser", "new user", "password", Role.USER), p)
        }

        withUser(USER_ID) { p ->
            val result = userController.getUser(newUser.id, p)
            assertEquals("new user", result.name)
            assertNull(result.mail)
        }
    }

    @Test
    fun `getUser with observer should show email`() {
        withUser(OBSERVER_ID) { p ->
            val result = userController.getUser(USER_ID, p)
            assertEquals("user1@ticktag.a", result.mail)
        }
    }

    @Test
    fun `getUser with common project should show email`() {
        withUser(USER_ID) { p ->
            val result = userController.getUser(OBSERVER_ID, p)
            assertEquals("Obelix Observer", result.name)
            assertEquals("observer@ticktag.a", result.mail)
        }
    }

    @Test
    fun `getUserImage with correct temp id should succeed`() {
        withUser(USER_ID) { p ->
            val result = userController.getUser(OBSERVER_ID, p)

            val resp = MockHttpServletResponse()
            userController.getUserImage(result.imageId, resp)
            assertThat(resp.status, `is`(200))
        }
    }

    @Test
    fun `getUserImage with expired temp id should fail`() {
        withUser(USER_ID) { p ->
            val now = Instant.ofEpochSecond(0)
            timeMachine.now = now
            val result = userController.getUser(OBSERVER_ID, p)

            val later = now.plus(Duration.ofDays(2))
            timeMachine.now = later

            val resp = MockHttpServletResponse()
            assertFailsWith(AccessDeniedException::class, {
                userController.getUserImage(result.imageId, resp)
            })
        }
    }

    @Test
    fun `getUserImage with invalid temp id should fail`() {
        withUser(USER_ID) { p ->
            val imageId = Base64.encodeBase64URLSafeString(byteArrayOf(0x00, 0x00, 0x00, 0x00))
            val resp = MockHttpServletResponse()
            assertFailsWith(AccessDeniedException::class, {
                userController.getUserImage(imageId, resp)
            })
        }
    }
}