package io.ticktag.restinterface.auth.controllers

import io.swagger.annotations.ApiOperation
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.auth.schema.LoginRequest
import io.ticktag.restinterface.auth.schema.LoginResult
import io.ticktag.service.user.services.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/auth")
open class AuthController @Inject constructor(
        private val userService: UserService
) {
    @PostMapping("login")
    @ApiOperation(value = "Obtain an auth token for the given credentials")
    open fun login(@RequestBody loginRequest: LoginRequest): LoginResult {
        val validLogin = userService.checkPassword(loginRequest.email, loginRequest.password)

        if (validLogin) {
            val token = "TODO generate this"
            return LoginResult(token)
        } else {
            // TODO actually return a *real* error here
            return LoginResult("error")
        }
    }
}
