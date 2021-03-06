package io.ticktag.service.member.dto

import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.ProjectRole
import java.time.Instant
import java.util.*

data class MemberResult(
        val userId: UUID,
        val projectId: UUID,
        val joinDate: Instant,
        val projectRole: ProjectRole,
        val defaultAssignmentTagId: UUID?
) {
    constructor(m: Member) : this(userId = m.user.id, projectId = m.project.id, joinDate = m.joinDate, projectRole = m.role, defaultAssignmentTagId = m.defaultAssignmentTag?.id)
}
