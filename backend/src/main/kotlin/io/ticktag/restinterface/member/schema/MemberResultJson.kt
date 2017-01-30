package io.ticktag.restinterface.member.schema

import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.service.member.dto.MemberResult
import java.time.Instant
import java.util.*

data class MemberResultJson(
        val userId: UUID,
        val projectId: UUID,
        val joinDate: Instant,
        val projectRole: ProjectRole,
        val defaultAssignmentTagId: UUID?
) {
    constructor(m: MemberResult) : this(userId = m.userId, projectId = m.projectId, joinDate = m.joinDate, projectRole = m.projectRole, defaultAssignmentTagId = m.defaultAssignmentTagId)
}