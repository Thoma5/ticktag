package io.ticktag.persistence.member

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.MemberKey
import io.ticktag.persistence.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

@TicktagRepository
interface MemberRepository : TicktagCrudRepository<Member, MemberKey> {
    fun findByUser(user: UUID, pageable: Pageable): Page<Project>
    fun findByProject(project: UUID, pageable: Pageable): Page<Project>

    fun findByUserIdAndProjectId(userId: UUID, projectId: UUID): Member?
}
