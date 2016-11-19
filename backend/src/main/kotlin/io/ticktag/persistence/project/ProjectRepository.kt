package io.ticktag.persistence.project

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

@TicktagRepository
interface ProjectRepository : TicktagCrudRepository<Project, UUID> {
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Project>
    fun findByMembersUserIdAndNameContainingIgnoreCase(userId: UUID, name: String, pageable: Pageable): Page<Project>
    fun countByMembersUserId(userId: UUID): Int
}