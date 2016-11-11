package io.ticktag.persistence.member

import io.ticktag.TicktagRepository
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.MemberKey
import io.ticktag.persistence.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

@TicktagRepository
interface ProjectRepository : JpaRepository<Member, MemberKey> {
    fun findByIdUID(name: String, pageable: Pageable): Page<Project>
    fun findByIdPID(name: String, pageable: Pageable): Page<Project>
}
