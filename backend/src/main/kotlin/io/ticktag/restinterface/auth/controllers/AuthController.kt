package io.ticktag.restinterface.auth.controllers

import io.swagger.annotations.ApiOperation
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.auth.schema.LoginRequest
import io.ticktag.restinterface.auth.schema.LoginResult
import io.ticktag.restinterface.auth.schema.WhoamiResult
import io.ticktag.service.Principal
import io.ticktag.service.user.services.UserService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.token.TokenService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.inject.Inject
import javax.inject.Named

@TicktagRestInterface
@RequestMapping("/auth")
open class AuthController @Inject constructor(
        private val userService: UserService,
        @Named("restAuthTokenService") private val tokenService: TokenService
) {
    @PostMapping("login")
    @ApiOperation(value = "Obtain an auth token for the given credentials")
    open fun login(@RequestBody loginRequest: LoginRequest): LoginResult {
        val validLogin = userService.checkPassword(loginRequest.email, loginRequest.password)
        if (validLogin != null) {
            return LoginResult(tokenService.allocateToken(validLogin.toString()).key)
        } else {
            // TODO actually return a *real* error here
            return LoginResult("error")
        }
    }

    @GetMapping("whoami")
    @ApiOperation(value = "Returns the logged-in user")
    open fun whoami(@AuthenticationPrincipal principal: Principal): WhoamiResult {
        userService.dummy()
        return WhoamiResult(principal.id, principal.authorities.toList())
    }
}
