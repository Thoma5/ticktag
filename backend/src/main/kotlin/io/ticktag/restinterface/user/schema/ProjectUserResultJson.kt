package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.user.dto.ProjectUserResult
import org.apache.commons.codec.binary.Base64
import java.time.Instant
import java.util.*

data class ProjectUserResultJson(
        val id: UUID,
        val name: String,
        val username: String,
        val mail: String?,
        val role: Role,
        val projectRole: ProjectRole,
        val imageId: String,
        val projectId: UUID,
        val joinDate: Instant,
        val disabled: Boolean
) {
    constructor(u: ProjectUserResult) : this(id = u.id, name = u.name, mail = u.mail, role = u.role, disabled = u.disabled, username = u.username, imageId = Base64.encodeBase64URLSafeString(u.imageId.data), projectRole = u.projectRole, projectId = u.projectId, joinDate = u.joinDate)
}