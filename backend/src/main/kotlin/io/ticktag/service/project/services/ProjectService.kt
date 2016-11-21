package io.ticktag.service.project.services

import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.ProjectResult
import io.ticktag.service.project.dto.UpdateProject
import org.springframework.data.domain.Pageable
import java.util.*

interface ProjectService {
    fun getProject(id: UUID): ProjectResult?
    fun listAllProjects(name: String, pageable: Pageable): List<ProjectResult>
    fun listUserProjects(userId: UUID, name: String, pageable: Pageable): List<ProjectResult>
    fun createProject(project: CreateProject): ProjectResult
    fun deleteProject(id: UUID)
    fun updateProject(id: UUID, project: UpdateProject) : ProjectResult
    fun getProjectCount(): Int
    fun getUserProjectCount(userId: UUID): Int
}