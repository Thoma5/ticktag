package io.ticktag.service.project.services.impl

import io.ticktag.TicktagService
import io.ticktag.library.base64ImageDecoder.Base64ImageDecoder
import io.ticktag.library.base64ImageDecoder.Image
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.project.entity.Project
import io.ticktag.service.*
import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.ProjectResult
import io.ticktag.service.project.dto.ProjectRoleResult
import io.ticktag.service.project.dto.UpdateProject
import io.ticktag.service.project.services.ProjectService
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
        private val projects: ProjectRepository,
        private val base64ImageDecoder: Base64ImageDecoder
) : ProjectService {
    companion object {
        val MAX_IMAGE_SIZE: Int = 150000
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun createProject(@Valid project: CreateProject): ProjectResult {
        val name = project.name
        val description = project.description
        var icon: ByteArray? = null
        var iconMimeInfo: String? = null
        if (project.icon != null) {
            val tempImg = base64ImageDecoder.decode(project.icon)
            if (tempImg.image.size > MAX_IMAGE_SIZE) throw TicktagValidationException(listOf(ValidationError("project.icon", ValidationErrorDetail.Other("maxsize"+ MAX_IMAGE_SIZE + "KB"))))
            icon = tempImg.image
            iconMimeInfo = tempImg.mimeType
        }
        val creationDate = Date()
        val newProject = Project.create(name, description, creationDate, iconMimeInfo, icon)
        projects.insert(newProject)
        return ProjectResult(newProject)
    }

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun getProject(@P("authProjectId") id: UUID): ProjectResult {
        return ProjectResult(projects.findOne(id) ?: throw NotFoundException())
    }

    @PreAuthorize(AuthExpr.OBSERVER)
    override fun listAllProjects(name: String, disabled: Boolean, pageable: Pageable): Page<ProjectResult> {
        val page = projects.findByNameContainingIgnoreCaseAndDisabledIs(name, disabled, pageable)
        val content = page.content.map(::ProjectResult)
        return PageImpl(content, pageable, page.totalElements)
    }

    @PreAuthorize(AuthExpr.ADMIN_OR_SELF)
    override fun listUserProjects(userId: UUID, name: String, disabled: Boolean, pageable: Pageable): Page<ProjectResult> {
        val page = projects.findByMembersUserIdAndNameContainingIgnoreCaseAndDisabledIs(userId, name, disabled, pageable)
        val content = page.content.map(::ProjectResult)
        return PageImpl(content, pageable, page.totalElements)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun deleteProject(id: UUID) {
        val projectToDelete = projects.findOne(id) ?: throw NotFoundException()
        projectToDelete.disabled = true
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
        if (project.disabled != null) {
            projectToUpdate.disabled = project.disabled
        }
        if (project.icon != null) {
            if (project.icon.isEmpty()) {
                projectToUpdate.iconMimeInfo = null
                projectToUpdate.icon = null
            } else {
                val tempImg = base64ImageDecoder.decode(project.icon)
                if (tempImg.image.size > MAX_IMAGE_SIZE) throw TicktagValidationException(listOf(ValidationError("project.icon", ValidationErrorDetail.Other("maxsize"+ MAX_IMAGE_SIZE + "KB"))))
                val icon= tempImg.image
                val iconMimeInfo= tempImg.mimeType
                projectToUpdate.iconMimeInfo = iconMimeInfo
                projectToUpdate.icon = icon
            }
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
    @PreAuthorize(AuthExpr.ADMIN)
    override fun listProjectRoles(): List<ProjectRoleResult> {
        return ProjectRole.values().map(::ProjectRoleResult)
    }


}