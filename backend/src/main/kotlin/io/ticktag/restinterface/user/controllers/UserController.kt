package io.ticktag.restinterface.user.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.persistence.user.entity.Role
import io.ticktag.restinterface.user.schema.*
import io.ticktag.service.Principal
import io.ticktag.service.user.dto.CreateUser
import io.ticktag.service.user.dto.TempImageId
import io.ticktag.service.user.dto.UpdateUser
import io.ticktag.service.user.services.UserService
import org.apache.commons.codec.binary.Base64
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.servlet.http.HttpServletResponse

@TicktagRestInterface
@RequestMapping("/user")
@Api(tags = arrayOf("user"), description = "user management")
open class UserController @Inject constructor(
        private val userService: UserService
) {
    companion object {
        val IMAGE_CACHE_DURATION = Duration.ofMinutes(10)
    }


    @PostMapping
    open fun createUser(@RequestBody req: CreateUserRequestJson,
                        @AuthenticationPrincipal principal: Principal
    ): UserResultJson {
        val create = CreateUser(mail = req.mail, name = req.name, password = req.password, role = req.role, username = req.username)
        val user = userService.createUser(create, principal)
        return UserResultJson(user)
    }

    @PutMapping(value = "/{id}")
    open fun updateUser(@PathVariable(name = "id") id: UUID,
                        @RequestBody req: UpdateUserRequestJson,
                        @AuthenticationPrincipal principal: Principal): UserResultJson {
        val user = userService.updateUser(principal, id, UpdateUser(mail = req.mail, name = req.name, password = req.password,
                role = req.role, disabled = req.disabled, oldPassword = req.oldPassword))
        return UserResultJson(user)
    }

    @GetMapping(value = "/{id}")
    open fun getUser(@PathVariable(name = "id") id: UUID,
                     @AuthenticationPrincipal principal: Principal
    ): UserResultJson {
        return UserResultJson(userService.getUser(id, principal))
    }

    @ResponseBody
    @GetMapping("/image/{imageId}")
    open fun getUserImage(@PathVariable("imageId") imageId: String, response: HttpServletResponse) {
        val dto = TempImageId(Base64.decodeBase64(imageId))
        val image = userService.getUserImage(dto)
        val cacheControlValue = CacheControl.maxAge(IMAGE_CACHE_DURATION.seconds, TimeUnit.SECONDS).headerValue
        response.setHeader("Cache-Control", cacheControlValue)
        response.contentType = MediaType.IMAGE_PNG_VALUE
        response.outputStream.write(image)
    }

    @GetMapping("/name/{name}")
    open fun getUserByUsername(@PathVariable(name = "name") username: String,
                               @AuthenticationPrincipal principal: Principal
    ): UserResultJson {
        return UserResultJson(userService.getUserByUsername(username, principal))
    }

    // TODO paging, filter, sorting
    @GetMapping
    open fun listUsers(@RequestParam(name = "page", defaultValue = "0", required = false) pageNumber: Int,
                        @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                        @RequestParam(name = "order", defaultValue = "NAME_ASC", required = false) order: List<UserSort>,
                        @RequestParam(name = "query", defaultValue = "", required = false) query: String,
                        @RequestParam(name = "role", defaultValue = "", required = false) role: Role?,
                        @RequestParam(name = "disabled", defaultValue = "false", required = false) disabled: Boolean,
                        @AuthenticationPrincipal principal: Principal
    ): Page<UserResultJson> {
        val pageRequest = PageRequest(pageNumber, size, Sort(order.map { it.order }))
        val page = userService.listUsers(query, role, disabled, principal, pageRequest)
        val content = page.content.map(::UserResultJson)
        return PageImpl(content, pageRequest, page.totalElements)
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteProject(@PathVariable(name = "id") id: UUID) {
        userService.deleteUser(id)
    }

    @GetMapping("/fuzzy")
    open fun listUsersFuzzy(
            @RequestParam(name = "projectId", required = true) projectId: UUID,
            @RequestParam(name = "q", required = true) query: String,
            @RequestParam(name = "order", required = true) order: List<UserSort>,
            @AuthenticationPrincipal principal: Principal
    ): List<UserResultJson> {
        val result = userService.listUsersFuzzy(projectId, query, PageRequest(0, 15, Sort(order.map { it.order })), principal)
        return result.map(::UserResultJson)
    }

    @GetMapping(value = "/roles")
    open fun listRoles(): List<RoleResultJson> {
        return userService.listRoles().map(::RoleResultJson)
    }
}
