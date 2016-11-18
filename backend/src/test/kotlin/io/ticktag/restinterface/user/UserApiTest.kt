package io.ticktag.restinterface.user

import io.ticktag.adminId
import io.ticktag.observerId
import io.ticktag.persistence.user.entity.Role
import io.ticktag.restinterface.user.controllers.UserController
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import io.ticktag.service.ApiBaseTest
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import javax.inject.Inject

class UserApiTest : ApiBaseTest() {
    @Inject
    lateinit var userController : UserController

    @Test(expected = AccessDeniedException::class)
    fun `createUser by non-admin should fail`() {
        withUser(observerId()) { ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", Role.USER, null)
            val res = userController.createUser(req)
        }
    }

    @Test
    fun `createUser by admin should create new user`() {
        withUser(adminId()) { ->
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

    // TODO migrate the other tests !!!
}