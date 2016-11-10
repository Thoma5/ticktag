package io.ticktag.service.project.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.project.entity.Project
import io.ticktag.service.AuthExpr
import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.ProjectResult
import io.ticktag.service.project.dto.UpdateProject
import io.ticktag.service.project.services.ProjectService
import org.springframework.data.domain.Pageable
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

    @PreAuthorize(AuthExpr.ADMIN) //TODO: Add access for project member
    override fun getProject(id: UUID): ProjectResult {
        return ProjectResult(projects.findById(id) ?: throw RuntimeException() /* TODO: change to not found */)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun listProjects(name: String, pageable: Pageable): List<ProjectResult> {
        if (name.isEmpty()) {
            return projects.findAll(pageable).content.map(::ProjectResult)
        } else {
            return projects.findByNameLike(name, pageable).content.map(::ProjectResult)
        }
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun listProjects(pageable: Pageable): List<ProjectResult> {
        return listProjects("", pageable)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun deleteProject(id: UUID) {
        val projectToDelete = projects.findById(id) ?: throw RuntimeException() //TODO: change to NOT FOUND (404)
        projects.delete(projectToDelete)
    }

    @PreAuthorize(AuthExpr.ADMIN) //TODO: Add access for project member
    override fun updateProject(id: UUID, project: UpdateProject): ProjectResult {
        val projectToUpdate = projects.findById(id) ?: throw RuntimeException() //TODO: change to NOT FOUND (404)
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