package io.ticktag.service.member

import io.ticktag.service.member.dto.CreateMember
import io.ticktag.service.member.dto.MemberResult
import io.ticktag.service.member.dto.UpdateMember
import java.util.*

interface MemberService {
    fun getMember(userId: UUID, projectId: UUID): MemberResult
    fun createMember(userId: UUID, projectId: UUID, createMember: CreateMember): MemberResult
    fun deleteMember(userId: UUID, projectId: UUID)
    fun updateMember(userId: UUID, projectId: UUID, member: UpdateMember): MemberResult
}