package io.ticktag.service.member.dto

import io.ticktag.persistence.member.entity.ProjectRole

data class CreateMember(
        val role: ProjectRole
)