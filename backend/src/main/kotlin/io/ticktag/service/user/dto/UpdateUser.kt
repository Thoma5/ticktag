package io.ticktag.service.user.dto

import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

// TODO trim input?
data class UpdateUser(
        @field:Size(min = 3, max = 255) val mail: String?,
        @field:Size(min = 3, max = 30) val name: String?,
        @field:Pattern(regexp = User.PASSWORD_REGEX) val password: String?,
        @field:Size(min = 1) val oldPassword: String?,
        val role: Role?,
        val image: String?,
        val disabled: Boolean?
)