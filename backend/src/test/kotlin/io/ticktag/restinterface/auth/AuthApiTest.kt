package io.ticktag.restinterface.auth

import io.ticktag.ADMIN_ID
import io.ticktag.USER_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.auth.controllers.AuthController
import io.ticktag.restinterface.auth.schema.LoginRequestJson
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.*
import org.junit.Test
import javax.inject.Inject

class AuthApiTest : ApiBaseTest() {
    @Inject lateinit var authController: AuthController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }


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
            val request = LoginRequestJson("admin@ticktag.a", "aaaa")
            val result = authController.login(request)
            assertNotNull(result.token)
            assertThat(result.token, `is`(not("")))
        }
    }

    @Test
    fun `Whoami with admin should return correct authorities`() {
        val id = ADMIN_ID
        val expectedAuthorities = listOf("USER", "OBSERVER", "ADMIN")

        withUser(id) { principal ->
            val result = authController.whoami(principal)
            assertThat(result.id, `is`(id))
            assertThat(result.authorities, `is`(expectedAuthorities))
        }
    }

    @Test
    fun `Whoami with user should return correct authorities`() {
        val id = USER_ID
        val expectedAuthorities = listOf("USER")

        withUser(id) { principal ->
            val result = authController.whoami(principal)
            assertThat(result.id, `is`(id))
            assertThat(result.authorities, `is`(expectedAuthorities))
        }
    }
}