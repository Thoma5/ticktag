package io.ticktag.restinterface.member.schema

import io.ticktag.persistence.member.entity.ProjectRole

data class CreateMemberRequestJson(
        val projectRole: ProjectRole
)