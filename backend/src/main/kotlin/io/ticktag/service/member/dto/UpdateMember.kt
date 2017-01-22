package io.ticktag.service.member.dto

import io.ticktag.persistence.member.entity.ProjectRole
import java.util.*

data class UpdateMember(
        val role: ProjectRole?,
        val defaultAssignmentTagId: UUID?
)