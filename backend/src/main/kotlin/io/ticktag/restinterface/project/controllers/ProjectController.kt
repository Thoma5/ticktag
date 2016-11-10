package io.ticktag.restinterface.project.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.project.schema.CreateProjectRequestJson
import io.ticktag.restinterface.project.schema.ProjectResultJson
import io.ticktag.service.project.dto.CreateProject
import io.ticktag.service.project.services.ProjectService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/project")
@Api(tags = arrayOf("project"), description = "project management")
open class ProjectController @Inject constructor(
        private val projectService: ProjectService
) {

    //TODO: adjust default values
    @GetMapping
    open fun list(@RequestParam(name = "page", defaultValue = "0", required = false) page: Int,
                  @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                  @RequestParam(name = "order", defaultValue = "name", required = false) order: String,
                  @RequestParam(name = "asc", defaultValue = "true", required = false) asc: Boolean,
                  @RequestParam(name = "name", defaultValue = "", required = false) name: String
    ): List<ProjectResultJson> {
        val ascOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        val sortOrder = Sort.Order(ascOrder, order).ignoreCase() //TODO: check if order is a valid column
        val pageRequest = PageRequest(page, size, Sort(sortOrder))
        return projectService.listProjects(name, pageRequest).map(::ProjectResultJson)
    }

    @PostMapping
    open fun create(@RequestBody req: CreateProjectRequestJson): ProjectResultJson {
        val project = projectService.createProject(CreateProject(req.name, req.description, req.icon))
        return ProjectResultJson(project)
    }

    @DeleteMapping(value = "/{id}")
    open fun delete(@PathVariable(name = "id") id: UUID) {
        projectService.deleteProject(id)
    }

    @PutMapping(value = "/{id}")
    open fun update(@PathVariable(name = "id") id: UUID,
                    @RequestBody req: CreateProjectRequestJson): ProjectResultJson {
        val project = projectService.updateProject(id, CreateProject(req.name, req.description, req.icon))
        return ProjectResultJson(project)
    }
}