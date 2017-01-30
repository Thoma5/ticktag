package io.ticktag.restinterface.project.schema

import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.service.project.dto.ProjectRoleResult


data class ProjectRoleResultJson(
        val role: ProjectRole
) {
    constructor(r: ProjectRoleResult) : this(role = r.role)
}