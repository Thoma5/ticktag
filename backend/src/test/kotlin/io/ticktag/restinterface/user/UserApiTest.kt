package io.ticktag.restinterface.user

import io.ticktag.ADMIN_ID
import io.ticktag.OBSERVER_ID
import io.ticktag.persistence.user.entity.Role
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.user.controllers.UserController
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Assert.fail
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import javax.inject.Inject

class UserApiTest : ApiBaseTest() {
    @Inject
    lateinit var userController : UserController

    @Test(expected = AccessDeniedException::class)
    fun `createUser by non-admin should fail`() {
        withUser(OBSERVER_ID) { ->
            val req = CreateUserRequestJson("a@b.com", "name", "password", "unique_test_user", Role.USER, null)
            userController.createUser(req)
        }
    }

    @Test
    fun `createUser by admin should create new user`() {
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

    // TODO migrate the other tests !!!
}