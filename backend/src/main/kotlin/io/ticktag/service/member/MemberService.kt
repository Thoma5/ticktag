package io.ticktag.service.member

import io.ticktag.service.member.dto.CreateMember
import io.ticktag.service.member.dto.MemberResult
import io.ticktag.service.member.dto.UpdateMember
import java.util.*

interface MemberService {
    /**
     * get a member. A member is a user who is working at a project
     */
    fun getMember(userId: UUID, projectId: UUID): MemberResult

    /**
     * create a member. Therefore an existing User can contribute to a project
     */
    fun createMember(userId: UUID, projectId: UUID, createMember: CreateMember): MemberResult

    /**
     * The member won't be deleted, his role will be removed and so he can't contributed to the Project anymore
     * All entities related to this member will still exist after the deletion
     */
    fun deleteMember(userId: UUID, projectId: UUID)

    /**
     * Update a role or a default assignment Tag for a member (A user in a project)
     */
    fun updateMember(userId: UUID, projectId: UUID, member: UpdateMember): MemberResult
}