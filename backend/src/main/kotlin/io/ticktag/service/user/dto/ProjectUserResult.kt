package io.ticktag.service.user.dto

import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.user.entity.Role
import java.time.Instant
import java.util.*

data class ProjectUserResult(
        val id: UUID,
        val mail: String?,
        val username: String,
        val name: String,
        val currentToken: UUID,
        val role: Role,
        val disabled: Boolean,
        val projectRole: ProjectRole,
        val imageId: TempImageId,
        val projectId: UUID,
        val joinDate: Instant
) {
    constructor(u: UserResult, m: Member) : this(id = u.id, mail = u.mail, name = u.name, currentToken = u.currentToken, role = u.role, disabled = u.disabled, username = u.username, imageId = u.imageId, projectRole = m.role, projectId = m.project.id, joinDate = m.joinDate)
}
