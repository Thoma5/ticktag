package io.ticktag.restinterface.user.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import io.ticktag.restinterface.user.schema.UserResultJson
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/user")
@Api(tags = arrayOf("user"), description = "user management")
open class UserController @Inject constructor(
        private val userService: UserService
) {

    @PostMapping
    open fun createUser(@RequestBody req: CreateUserRequestJson): UserResultJson {
        val user = userService.createUser(CreateUser(mail = req.mail, name = req.name, password = req.password))
        return UserResultJson(user)
    }

    @GetMapping
    open fun listUsers(): List<UserResultJson> {
        return userService.listUsers().map(::UserResultJson)
    }
}
