package io.ticktag.restinterface.user.schema

data class CreateUserRequestJson(
        val mail: String,
        val name: String,
        val password: String
)
