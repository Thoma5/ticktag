package io.ticktag.service.member.dto

import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.ProjectRole
import java.util.*

data class MemberResult(
        val userId: UUID,
        val projectId: UUID,
        val joinDate: Date,
        val projectRole: ProjectRole
) {
    constructor(m: Member) : this(userId = m.user.id, projectId = m.project.id, joinDate = m.joinDate, projectRole = m.role)
}
