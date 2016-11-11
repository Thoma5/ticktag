package io.ticktag.restinterface.user.schema

data class UpdateUserRequestJson(
        val mail: String?,
        val name: String?,
        val password: String?
)
