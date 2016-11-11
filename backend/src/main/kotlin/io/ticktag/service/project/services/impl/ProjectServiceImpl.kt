package io.ticktag.service.project.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.project.entity.Project
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.ProjectResult
import io.ticktag.service.project.dto.UpdateProject
import io.ticktag.service.project.services.ProjectService
import org.springframework.data.domain.Pageable
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class ProjectServiceImpl @Inject constructor(
    private val projects: ProjectRepository
) : ProjectService {

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createProject(project: CreateProject): ProjectResult {
        val name = project.name
        val description = project.description
        val icon = project.icon
        val creationDate = Date()
        val project = Project.create(name, description, creationDate, icon)
        projects.insert(project)
        return ProjectResult(project)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun getProject(@P("authProjectId") id: UUID): ProjectResult {
        return ProjectResult(projects.findOne(id) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun listAllProjects(name: String, pageable: Pageable): List<ProjectResult> {
        return projects.findByNameLike("%$name%", pageable).content.map(::ProjectResult)
    }

    @PreAuthorize(AuthExpr.USER)
    override fun listUserProjects(principal: Principal, name: String, pageable: Pageable): List<ProjectResult> {
        return projects.findByMembersUserIdAndNameLike(principal.id, "%$name%", pageable).content.map(::ProjectResult)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun deleteProject(id: UUID) {
        val projectToDelete = projects.findOne(id) ?: throw NotFoundException()
        projects.delete(projectToDelete)
    }

    @PreAuthorize(AuthExpr.PROJECT_ADMIN)
    override fun updateProject(@P("authProjectId") id: UUID, project: UpdateProject): ProjectResult {
        val projectToUpdate = projects.findOne(id) ?: throw NotFoundException()
        if (project.name != null) {
            projectToUpdate.name = project.name
        }
        if (project.description != null) {
            projectToUpdate.description = project.description
        }
        if (project.icon != null) {
            projectToUpdate.icon = project.icon
        }
        return ProjectResult(projectToUpdate)
    }
}