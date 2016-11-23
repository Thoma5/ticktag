package io.ticktag.service.user.dto

import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

// TODO trim input?
data class CreateUser(
        @field:Size(min = 3, max = 255) val mail: String,
        @field:Pattern(regexp = User.USERNAME_REGEX) val username: String,
        @field:Size(min = 3, max = 30) val name: String,
        @field:Size(min = 1) val password: String,
        val role: Role,
        val profilePic: ByteArray?
)
