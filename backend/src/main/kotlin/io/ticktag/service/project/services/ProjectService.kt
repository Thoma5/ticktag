package io.ticktag.service.project.services

import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.ProjectResult
import org.springframework.data.domain.Pageable
import java.util.*

interface ProjectService {
    fun getProject(id: UUID): ProjectResult?
    fun listProjects(name: String, pageable: Pageable): List<ProjectResult>
    fun listProjects(pageable: Pageable): List<ProjectResult>
    fun createProject(project: CreateProject): ProjectResult
    fun deleteProject(id: UUID)
    fun updateProject(id: UUID, project: CreateProject) : ProjectResult
}