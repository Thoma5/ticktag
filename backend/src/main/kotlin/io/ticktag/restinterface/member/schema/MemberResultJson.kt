package io.ticktag.restinterface.member.schema

import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.service.member.dto.MemberResult
import java.util.*

data class MemberResultJson(
        val uID: UUID,
        val pID: UUID,
        val joinDate: Date,
        val projectRole: ProjectRole
) {
    constructor(m: MemberResult) : this(uID = m.uID, pID = m.pID, joinDate = m.joinDate, projectRole = m.projectRole)
}