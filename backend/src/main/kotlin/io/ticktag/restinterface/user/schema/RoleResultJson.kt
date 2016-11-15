package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.user.dto.RoleResult


data class RoleResultJson(
        val role: Role
) {
    constructor(r: RoleResult) : this(role = r.role)
}