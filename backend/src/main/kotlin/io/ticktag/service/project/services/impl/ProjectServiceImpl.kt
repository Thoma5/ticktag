package io.ticktag.service.project.services.impl

import io.ticktag.TicktagService
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.project.entity.Project
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.ProjectResult
import io.ticktag.service.project.dto.UpdateProject
import io.ticktag.service.project.services.ProjectService
import io.ticktag.service.user.dto.UserResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class ProjectServiceImpl @Inject constructor(
        private val projects: ProjectRepository
) : ProjectService {

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createProject(@Valid project: CreateProject): ProjectResult {
        val name = project.name
        val description = project.description
        val icon = project.icon
        val creationDate = Date()
        val newProject = Project.create(name, description, creationDate, icon)
        projects.insert(newProject)
        return ProjectResult(newProject)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun getProject(@P("authProjectId") id: UUID): ProjectResult {
        return ProjectResult(projects.findOne(id) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.OBSERVER)
    override fun listAllProjects(name: String, pageable: Pageable): Page<ProjectResult> {
        val page = projects.findByNameContainingIgnoreCase(name, pageable)
        val content = page.content.map(::ProjectResult)
        return PageImpl(content, pageable, page.totalElements)
    }

    @PreAuthorize(AuthExpr.ADMIN_OR_SELF)
    override fun listUserProjects(userId: UUID, name: String, pageable: Pageable): Page<ProjectResult> {
        val page = projects.findByMembersUserIdAndNameContainingIgnoreCase(userId, name, pageable)
        val content = page.content.map(::ProjectResult)
        return PageImpl(content, pageable, page.totalElements)
    }
    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listUserProjectUsers(@P("authProjectId") id: UUID): List<UserResult> {
        val project = projects.findOne(id) ?: throw NotFoundException()
        return project.members.map(Member::user).map(::UserResult)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun deleteProject(id: UUID) {
        val projectToDelete = projects.findOne(id) ?: throw NotFoundException()
        projects.delete(projectToDelete)
    }

    @PreAuthorize(AuthExpr.PROJECT_ADMIN)
    override fun updateProject(@P("authProjectId") id: UUID, @Valid project: UpdateProject): ProjectResult {
        val projectToUpdate = projects.findOne(id) ?: throw NotFoundException()
        if (project.name != null) {
            projectToUpdate.name = project.name
        }
        if (project.description != null) {
            projectToUpdate.description = project.description
        }
        if (project.icon != null) {
            if (project.icon.isEmpty()) {
                projectToUpdate.icon = null
            }
            projectToUpdate.icon = project.icon
        }
        return ProjectResult(projectToUpdate)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun getProjectCount(): Int {
        return projects.count()
    }

    @PreAuthorize(AuthExpr.ADMIN_OR_SELF)
    override fun getUserProjectCount(userId: UUID): Int {
        return projects.countByMembersUserId(userId)
    }

}