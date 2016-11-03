package io.ticktag.service.user.dto

import javax.validation.constraints.Size

// TODO trim input?
data class CreateUser(
        @field:Size(min = 3, max = 255) val mail: String,
        @field:Size(min = 3, max = 30) val name: String,
        @field:Size(min = 1) val password: String
)
