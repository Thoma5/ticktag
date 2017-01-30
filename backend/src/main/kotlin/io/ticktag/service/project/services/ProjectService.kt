package io.ticktag.service.project.services

import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.ProjectResult
import io.ticktag.service.project.dto.ProjectRoleResult
import io.ticktag.service.project.dto.UpdateProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ProjectService {
    /**
     * Get a Project with specified UUID if it exits
     */
    fun getProject(id: UUID): ProjectResult?

    /**
     * List all Projects
     */
    fun listAllProjects(name: String, disabled: Boolean, pageable: Pageable): Page<ProjectResult>

    /**
     * List all projects where a given User is a member
     */
    fun listUserProjects(userId: UUID, name: String, disabled: Boolean, pageable: Pageable): Page<ProjectResult>

    /**
     * Create a project with given properties
     */
    fun createProject(project: CreateProject): ProjectResult

    /**
     * Deletes a project permanently
     */
    fun deleteProject(id: UUID)

    /**
     * Updates the properties of a project
     */
    fun updateProject(id: UUID, project: UpdateProject): ProjectResult

    /**
     * List all projects roles of a project
     */
    fun listProjectRoles(): List<ProjectRoleResult>
}