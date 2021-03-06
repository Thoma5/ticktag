package io.ticktag.restinterface.auth.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.ticktag.TicktagRestInterface
import io.ticktag.persistence.user.entity.Role
import io.ticktag.restinterface.RestAuthToken
import io.ticktag.restinterface.auth.schema.LoginRequestJson
import io.ticktag.restinterface.auth.schema.LoginResultJson
import io.ticktag.restinterface.auth.schema.WhoamiResultJson
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
@Api(tags = arrayOf("auth"), description = "authentication")
open class AuthController @Inject constructor(
        private val userService: UserService,
        @Named("restAuthTokenService") private val tokenService: TokenService
) {
    @PostMapping("login")
    @ApiOperation(value = "Obtain an auth token for the given credentials")
    open fun login(@RequestBody loginRequest: LoginRequestJson): LoginResultJson {
        val validLogin = userService.checkPassword(loginRequest.email, loginRequest.password)
        if (validLogin != null) {
            val token = RestAuthToken(validLogin.id, validLogin.currentToken)
            return LoginResultJson(tokenService.allocateToken(token.toString()).key)
        } else {
            return LoginResultJson("")
        }
    }

    @GetMapping("whoami")
    @ApiOperation(value = "Returns the logged-in user")
    open fun whoami(@AuthenticationPrincipal principal: Principal): WhoamiResultJson {
        val authorities = if (principal.role == null) {
            emptyList()
        } else {
            Role.values().filter { principal.role.includesRole(it) }.map(Role::toString)
        }
        return WhoamiResultJson(principal.id, authorities)
    }
}
