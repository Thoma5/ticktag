package io.ticktag.service.member

import io.ticktag.service.member.dto.CreateMember
import io.ticktag.service.member.dto.MemberResult
import io.ticktag.service.member.dto.UpdateMember
import java.util.*

interface MemberService {
    fun getMember(uID: UUID, pID: UUID): MemberResult
    fun createMember(uID: UUID, pID: UUID, member: CreateMember): MemberResult
    fun deleteMember(uID: UUID, pID: UUID)
    fun updateMember(uID: UUID, pID: UUID, member: UpdateMember): MemberResult
}