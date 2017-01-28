package io.ticktag.service.member.services

import io.ticktag.TicktagService
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.MemberKey
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.entity.AssignmentTag
import io.ticktag.persistence.user.UserRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.member.MemberService
import io.ticktag.service.member.dto.CreateMember
import io.ticktag.service.member.dto.MemberResult
import io.ticktag.service.member.dto.UpdateMember
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.time.Instant
import java.util.*
import javax.inject.Inject

@TicktagService
open class MemberServiceImpl @Inject constructor(
        private val members: MemberRepository,
        private val users: UserRepository,
        private val projects: ProjectRepository,
        private val assignmentTagRepository: AssignmentTagRepository
) : MemberService {
    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun getMember(userId: UUID, @P("authProjectId") projectId: UUID): MemberResult {
        val user = users.findOne(userId) ?: throw NotFoundException()
        val project = projects.findOne(projectId) ?: throw NotFoundException()
        val member = members.findOne(MemberKey.create(user, project)) ?: throw NotFoundException()
        return MemberResult(member)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createMember(userId: UUID, projectId: UUID, createMember: CreateMember): MemberResult {
        //since this is admin only, even disabled users and disabled projects should be usable
        val user = users.findOne(userId) ?: throw NotFoundException()
        val project = projects.findOne(projectId) ?: throw NotFoundException()
        val defaultAssignmentTag:AssignmentTag?
        if (createMember.defaultAssignmentTagId != null){
            defaultAssignmentTag =  assignmentTagRepository.findOne(createMember.defaultAssignmentTagId)
        }else{
            defaultAssignmentTag= null
        }
        val member = Member.create(user, project, createMember.role, Instant.now(),defaultAssignmentTag)
        members.insert(member)
        return MemberResult(member)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun deleteMember(userId: UUID, projectId: UUID) {
        val user = users.findOne(userId) ?: throw NotFoundException()
        val project = projects.findOne(projectId) ?: throw NotFoundException()
        val memberToDelete = members.findOne(MemberKey.create(user, project)) ?: throw NotFoundException()
        memberToDelete.role = ProjectRole.NONE
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun updateMember(userId: UUID, projectId: UUID, member: UpdateMember): MemberResult {
        val user = users.findOne(userId) ?: throw NotFoundException()
        val project = projects.findOne(projectId) ?: throw NotFoundException()

        val memberToUpdate = members.findOne(MemberKey.create(user, project)) ?: throw NotFoundException()
        if (member.role != null) {
            memberToUpdate.role = member.role
        }

        if (member.defaultAssignmentTagId != null){
            memberToUpdate.defaultAssignmentTag =  assignmentTagRepository.findOne(member.defaultAssignmentTagId)
        }
        return MemberResult(memberToUpdate)
    }

}