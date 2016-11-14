package io.ticktag.service.member.dto

import io.ticktag.persistence.member.entity.ProjectRole

data class UpdateMember(
        val role: ProjectRole?
)