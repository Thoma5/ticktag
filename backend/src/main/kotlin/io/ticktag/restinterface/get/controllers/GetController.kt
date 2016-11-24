package io.ticktag.restinterface.get.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.get.schema.GetResultJson
import io.ticktag.restinterface.user.schema.UserResultJson
import io.ticktag.service.user.services.UserService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/get")
@Api(tags = arrayOf("get"), description = "get literally anything")
open class GetController @Inject constructor(
        private val userService: UserService
) {
    @GetMapping
    open fun get(
            @RequestParam("users", required = true) userIds: List<UUID>
    ): GetResultJson {
        return GetResultJson(
                users = if (userIds.isEmpty()) emptyMap() else userService.getUsers(userIds).mapValues { UserResultJson(it.value) }
        )
    }
}