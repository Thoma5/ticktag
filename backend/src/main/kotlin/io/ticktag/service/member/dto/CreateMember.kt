package io.ticktag.service.member.dto

import java.util.*
import javax.management.relation.Role
import javax.validation.constraints.Size

data class CreateMember(
        val uID: UUID,
        val pID: UUID,
        val role: Role, //TODO: @field stuff
        @field:Size(max = 204800) val icon: ByteArray?
)