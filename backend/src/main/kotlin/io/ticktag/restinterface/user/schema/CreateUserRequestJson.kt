package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.user.entity.Role

data class CreateUserRequestJson(
        val mail: String,
        val name: String,
        val password: String,
        val role:Role
)
