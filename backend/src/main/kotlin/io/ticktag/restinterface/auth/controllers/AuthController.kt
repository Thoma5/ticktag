package io.ticktag.restinterface.auth.controllers

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.auth.schema.LoginRequest
import io.ticktag.restinterface.auth.schema.LoginResult
import io.ticktag.service.auth.services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@TicktagRestInterface
@RequestMapping("/auth")
open class AuthController(
        private val authService: AuthService
) {
    @PostMapping("login")
    @ApiOperation(httpMethod = "POST", value = "Obtain an auth token for the given credentials", nickname = "login")
    open fun login(@RequestBody @ApiParam("loginRequest") LoginRequest: LoginRequest): LoginResult {
        val validLogin = authService.isValidLogin(LoginRequest.email, LoginRequest.password)

        if (validLogin) {
            val token = "TODO generate this"
            return LoginResult(token)
        } else {
            // TODO actually return a *real* error here
            return LoginResult("error")
        }
    }
}
