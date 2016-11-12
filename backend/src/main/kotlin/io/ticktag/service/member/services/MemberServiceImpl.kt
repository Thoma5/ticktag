package io.ticktag.service.member.services

import io.ticktag.TicktagService
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.MemberKey
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.member.MemberService
import io.ticktag.service.member.dto.CreateMember
import io.ticktag.service.member.dto.MemberResult
import io.ticktag.service.member.dto.UpdateMember
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class MemberServiceImpl @Inject constructor(
        private val members: MemberRepository,
        private val users: UserRepository,
        private val projects: ProjectRepository
) : MemberService {
    @PreAuthorize(AuthExpr.ADMIN)
    override fun getMember(uID: UUID, pID: UUID): MemberResult {
        var user = users.findOne(uID) ?: throw NotFoundException()
        var project = projects.findOne(pID) ?: throw NotFoundException()
        var member = members.findOne(MemberKey.create(user, project)) ?: throw NotFoundException();
        members.insert(member)
        return MemberResult(member)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createMember(uID: UUID, pID: UUID, member: CreateMember): MemberResult {
        var user = users.findOne(uID) ?: throw NotFoundException()
        var project = projects.findOne(pID) ?: throw NotFoundException()
        var member = Member.create(user, project, member.role, Date())
        members.insert(member)
        return MemberResult(member)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun deleteMember(uID: UUID, pID: UUID) {
        val user = users.findOne(uID) ?: throw NotFoundException()
        val project = projects.findOne(pID) ?: throw NotFoundException()
        val memberToDelete = members.findOne(MemberKey.create(user, project)) ?: throw NotFoundException()
        members.delete(memberToDelete)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun updateMember(uID: UUID, pID: UUID, member: UpdateMember): MemberResult {
        val user = users.findOne(uID) ?: throw NotFoundException()
        val project = projects.findOne(pID) ?: throw NotFoundException()
        val memberToUpdate = members.findOne(MemberKey.create(user, project)) ?: throw NotFoundException()
        if (member.role != null) {
            memberToUpdate.role = member.role
        }
        return MemberResult(memberToUpdate)
    }

}