package io.ticktag.restinterface.user.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.user.schema.*
import io.ticktag.service.Principal
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.dto.UpdateUser
import io.ticktag.service.user.services.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
        val user = userService.createUser(CreateUser(mail = req.mail, name = req.name, password = req.password, role = req.role, username = req.username))
        return UserResultJson(user)
    }

    @PutMapping(value = "/{id}")
    open fun updateUser(@PathVariable(name = "id") id: UUID,
                        @RequestBody req: UpdateUserRequestJson,
                        @AuthenticationPrincipal principal: Principal): UserResultJson {
        val user = userService.updateUser(principal, id, UpdateUser(mail = req.mail, name = req.name, password = req.password,
                role = req.role, oldPassword = req.oldPassword))
        return UserResultJson(user)
    }

    @GetMapping(value = "/{id}")
    open fun getUser(@PathVariable(name = "id") id: UUID): UserResultJson {
        return UserResultJson(userService.getUser(id))
    }

    @GetMapping("/{id}/image")
    open fun getUserImage(@PathVariable("id") id: UUID): UserImageJson {
        val image = userService.getUserImage(id)
        return UserImageJson(String(Base64.getEncoder().encode(image), charset("ASCII")))
    }

    @GetMapping("/name/{name}")
    open fun getUserByUsername(@PathVariable(name = "name") username: String): UserResultJson {
        return UserResultJson(userService.getUserByUsername(username))
    }

    // TODO paging, filter, sorting
    @GetMapping
    open fun listUsers(): List<UserResultJson> {
        return userService.listUsers().map(::UserResultJson)
    }

    @GetMapping("/fuzzy")
    open fun listUsersFuzzy(
            @RequestParam(name="projectId", required = true) projectId: UUID,
            @RequestParam(name="q", required = true) query: String,
            @RequestParam(name="order", required = true) order: List<UserSort>): List<UserResultJson> {
        val result = userService.listUsersFuzzy(projectId, query, PageRequest(0, 15, Sort(order.map { it.order })))
        return result.map(::UserResultJson)
    }

    @GetMapping(value = "/roles")
    open fun listRoles(): List<RoleResultJson> {
        return userService.listRoles().map(::RoleResultJson)
    }
}
