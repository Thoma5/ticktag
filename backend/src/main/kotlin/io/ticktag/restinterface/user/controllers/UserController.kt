package io.ticktag.restinterface.user.controllers

import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import io.ticktag.restinterface.user.schema.UserResultJson
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.services.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/users")
open class UserController @Inject constructor(
        private val userService: UserService
) {

    @PostMapping
    open fun create(@RequestBody req: CreateUserRequestJson): UserResultJson {
        val user = userService.createUser(CreateUser(mail = req.mail, name = req.name, password = req.password))
        return UserResultJson(user)
    }
}
