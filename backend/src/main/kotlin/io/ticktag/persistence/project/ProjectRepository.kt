package io.ticktag.persistence.project

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@TicktagRepository
interface ProjectRepository : TicktagCrudRepository<Project> {
    fun findByNameLike(name: String, pageable: Pageable): Page<Project>
}