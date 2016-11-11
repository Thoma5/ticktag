package io.ticktag.service.member.dto

import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.user.entity.User
import java.util.*

data class MemberResult(
        val uID: UUID,
        val pID: UUID,
        val joinDate: Date,
        val role: ProjectRole
) {
    constructor(m: Member) : this(uID = m.id.uID, pID = m.id.pID, joinDate = m.joinDate, role = m.role)
}
