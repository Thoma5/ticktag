package io.ticktag.restinterface.user.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.user.schema.CreateUserRequestJson
import io.ticktag.restinterface.user.schema.RoleResultJson
import io.ticktag.restinterface.user.schema.UpdateUserRequestJson
import io.ticktag.restinterface.user.schema.UserResultJson
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.dto.UpdateUser
import io.ticktag.service.user.services.UserService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/user")
@Api(tags = arrayOf("user"), description = "user management")
open class UserController @Inject constructor(
        private val userService: UserService
) {

    @PostMapping
    open fun createUser(@RequestBody req: CreateUserRequestJson): UserResultJson {
        val user = userService.createUser(CreateUser(mail = req.mail, name = req.name, password = req.password, role = req.role, profilePic = req.profilePic))
        //test
        return UserResultJson(user)
    }

    @PutMapping(value = "/{id}")
    open fun updateUser(@PathVariable(name = "id") id: UUID,
                        @RequestBody req: UpdateUserRequestJson): UserResultJson {
        val user = userService.updateUser(id, UpdateUser(mail = req.mail, name = req.name, password = req.password,
                role = req.role, profilePic = req.profilePic, oldPassword = req.oldPassword))
        return UserResultJson(user)
    }

    @GetMapping
    open fun listUsers(): List<UserResultJson> {
        return userService.listUsers().map(::UserResultJson)
    }


    @GetMapping(value = "/roles")
    open fun listRoles(): List<RoleResultJson> {
        return userService.listRoles().map(::RoleResultJson)
    }
}
