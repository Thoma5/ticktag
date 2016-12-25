package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.user.entity.Role

data class UpdateUserRequestJson(
        val mail: String?,
        val name: String?,
        val password: String?,
        val oldPassword: String?,
        val role: Role?,
        val disabled: Boolean
)
