package io.ticktag.restinterface.member.schema

import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.service.member.dto.MemberResult
import java.util.*

data class MemberResultJson(
        val userId: UUID,
        val projectId: UUID,
        val joinDate: Date,
        val projectRole: ProjectRole
) {
    constructor(m: MemberResult) : this(userId = m.userId, projectId = m.projectId, joinDate = m.joinDate, projectRole = m.projectRole)
}