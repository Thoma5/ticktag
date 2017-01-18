package io.ticktag.restinterface.member.schema

import io.ticktag.persistence.member.entity.ProjectRole
import java.util.*

data class UpdateMemberRequestJson(
        val projectRole: ProjectRole?,
        val defaultAssignmentTagId: UUID?
)