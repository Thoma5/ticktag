package io.ticktag.restinterface.auth

import io.ticktag.adminId
import io.ticktag.restinterface.auth.controllers.AuthController
import io.ticktag.restinterface.auth.schema.LoginRequestJson
import io.ticktag.service.ApiBaseTest
import io.ticktag.userId
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Test
import javax.inject.Inject

class AuthApiTest : ApiBaseTest() {
    @Inject lateinit var authController: AuthController

    @Test
    fun `Login with invalid credentials should fail`() {
        withoutUser {
            val request = LoginRequestJson("a@a.a", "bad password")
            val result = authController.login(request)
            assertEquals(result.token, "")
        }
    }

    @Test
    fun `Login with valid credentials should return auth token`() {
        withoutUser {
            val request = LoginRequestJson("a@a.a", "aaaa")
            val result = authController.login(request)
            assertNotNull(result.token)
            assertThat(result.token, `is`(not("")))
        }
    }

    @Test
    fun `Whoami with admin should return correct authorities`() {
        val id = adminId()
        val expectedAuthorities = listOf("USER", "OBSERVER", "ADMIN")

        withUser(id) { principal ->
            val result = authController.whoami(principal)
            assertThat(result.id, `is`(id))
            assertThat(result.authorities, `is`(expectedAuthorities))
        }
    }

    @Test
    fun `Whoami with user should return correct authorities`() {
        val id = userId()
        val expectedAuthorities = listOf("USER")

        withUser(id) { principal ->
            val result = authController.whoami(principal)
            assertThat(result.id, `is`(id))
            assertThat(result.authorities, `is`(expectedAuthorities))
        }
    }
}