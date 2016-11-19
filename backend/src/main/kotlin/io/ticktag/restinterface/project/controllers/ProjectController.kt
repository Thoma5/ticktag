package io.ticktag.restinterface.project.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.generic.CountJson
import io.ticktag.restinterface.project.schema.CreateProjectRequestJson
import io.ticktag.restinterface.project.schema.ProjectResultJson
import io.ticktag.restinterface.project.schema.ProjectSort
import io.ticktag.restinterface.project.schema.UpdateProjectRequestJson
import io.ticktag.restinterface.tickettaggroup.schema.TicketTagGroupResultJson
import io.ticktag.restinterface.timecategory.schema.TimeCategoryJson
import io.ticktag.restinterface.timecategory.schema.TimeCategorySort
import io.ticktag.service.Principal
import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.dto.UpdateProject
import io.ticktag.service.project.services.ProjectService
import io.ticktag.service.tickettaggroup.service.TicketTagGroupService
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
        private val ticketTagGroupService: TicketTagGroupService
        ) {
    //TODO: adjust default values
    @GetMapping
    open fun listProjects(@RequestParam(name = "page", defaultValue = "0", required = false) page: Int,
                          @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                          @RequestParam(name = "order", defaultValue = "NAME", required = false) order: ProjectSort,
                          @RequestParam(name = "asc", defaultValue = "true", required = false) asc: Boolean,
                          @RequestParam(name = "name", defaultValue = "", required = false) name: String,
                          @RequestParam(name = "all", defaultValue = "false", required = false) all: Boolean,
                          @AuthenticationPrincipal principal: Principal
    ): List<ProjectResultJson> {
        val ascOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        val sortOrder = Sort.Order(ascOrder, order.fieldName).ignoreCase()
        val pageRequest = PageRequest(page, size, Sort(sortOrder))

        return if (all) {
            projectService.listAllProjects(name, pageRequest)
        } else {
            projectService.listUserProjects(principal, name, pageRequest)
        }.map(::ProjectResultJson)
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

    @GetMapping(value = "/{id}/tickettaggroups")
    open fun listTicketTagGroups(@PathVariable(name = "id") id: UUID): List<TicketTagGroupResultJson> {
        return ticketTagGroupService.listTicketTagGroups(id).map(::TicketTagGroupResultJson)
    }

    @GetMapping(value = "/{id}/timecategories")
    open fun listProjectTimeCategories(@PathVariable(name = "id") projectId: UUID,
                                       @RequestParam(name = "page", defaultValue = "0", required = false) page: Int,
                                       @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                                       @RequestParam(name = "order", defaultValue = "NAME", required = false) order: TimeCategorySort,
                                       @RequestParam(name = "asc", defaultValue = "true", required = false) asc: Boolean,
                                       @RequestParam(name = "name", defaultValue = "", required = false) name: String
    ): List<TimeCategoryJson> {
        val ascOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        val sortOrder = Sort.Order(ascOrder, order.fieldName).ignoreCase()
        val pageRequest = PageRequest(page, size, Sort(sortOrder))

        return projectService.listProjectTimeCategories(projectId, name, pageRequest).map(::TimeCategoryJson)
    }

    @GetMapping(value = "/count")
    open fun getProjectsCount(@RequestParam(name = "all", defaultValue = "false", required = false) all: Boolean,
                              @AuthenticationPrincipal principal: Principal): CountJson {
        return if (all) {
            CountJson(projectService.getProjectCount())
        } else {
            CountJson(projectService.getUserProjectCount(principal))
        }
    }
}