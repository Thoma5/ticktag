package io.ticktag.restinterface.project.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.generic.CountJson
import io.ticktag.restinterface.project.schema.CreateProjectRequestJson
import io.ticktag.restinterface.project.schema.ProjectResultJson
import io.ticktag.restinterface.project.schema.ProjectSort
import io.ticktag.restinterface.project.schema.UpdateProjectRequestJson
import io.ticktag.restinterface.user.schema.UserResultJson
import io.ticktag.service.Principal
import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.UpdateProject
import io.ticktag.service.project.services.ProjectService
import io.ticktag.service.user.services.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/project")
@Api(tags = arrayOf("project"), description = "project management")
open class ProjectController @Inject constructor(
        private val projectService: ProjectService,
        private val userService: UserService
) {
    //TODO: adjust default values
    @GetMapping
    open fun listProjects(@RequestParam(name = "page", defaultValue = "0", required = false) pageNumber: Int,
                          @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                          @RequestParam(name = "order", defaultValue = "NAME", required = false) order: ProjectSort,
                          @RequestParam(name = "asc", defaultValue = "true", required = false) asc: Boolean,
                          @RequestParam(name = "name", defaultValue = "", required = false) name: String,
                          @RequestParam(name = "all", defaultValue = "false", required = false) all: Boolean,
                          @AuthenticationPrincipal principal: Principal
    ): Page<ProjectResultJson> {
        val ascOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        val sortOrder = Sort.Order(ascOrder, order.fieldName).ignoreCase()
        val pageRequest = PageRequest(pageNumber, size, Sort(sortOrder))

        return if (all) {
            val page = projectService.listAllProjects(name, pageRequest)
            val content = page.content.map(::ProjectResultJson)
            PageImpl(content, pageRequest, page.totalElements)
        } else {
            val page = projectService.listUserProjects(principal.id, name, pageRequest)
            val content = page.content.map(::ProjectResultJson)
            PageImpl(content, pageRequest, page.totalElements)
        }
    }

    @GetMapping(value = "/{id}/users")
    open fun listProjectUsers(@PathVariable(name = "id") id: UUID,
                              @AuthenticationPrincipal principal: Principal
    ): List<UserResultJson> {
        return userService.listUsersInProject(id, principal).map(::UserResultJson)
    }

    @PostMapping
    open fun createProject(@RequestBody req: CreateProjectRequestJson): ProjectResultJson {
        val project = projectService.createProject(CreateProject(req.name, req.description, req.icon))
        return ProjectResultJson(project)
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteProject(@PathVariable(name = "id") id: UUID) {
        projectService.deleteProject(id)
    }

    @PutMapping(value = "/{id}")
    open fun updateProject(@PathVariable(name = "id") id: UUID,
                           @RequestBody req: UpdateProjectRequestJson): ProjectResultJson {
        val project = projectService.updateProject(id, UpdateProject(req.name, req.description, req.icon))
        return ProjectResultJson(project)
    }

    @GetMapping(value = "/count")
    open fun getProjectsCount(@RequestParam(name = "all", defaultValue = "false", required = false) all: Boolean,
                              @AuthenticationPrincipal principal: Principal): CountJson {
        return if (all) {
            CountJson(projectService.getProjectCount())
        } else {
            CountJson(projectService.getUserProjectCount(principal.id))
        }
    }
}