package io.ticktag.restinterface.member.schema

import io.ticktag.persistence.member.entity.ProjectRole
import java.util.*

data class CreateMemberRequestJson(
        val projectRole: ProjectRole,
        val defaultAssignmentTagId: UUID?
)