package io.ticktag.restinterface.assignmenttag.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.assignmenttag.schema.*
import io.ticktag.service.assignmenttag.dto.*
import io.ticktag.service.assignmenttag.services.AssignmentTagService

import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/assignmenttag")
@Api(tags = arrayOf("assignmenttag"), description = "assignment tags describes the relation between a user and a ticket")
open class AssignmentTagController @Inject constructor(
        private val assignmentTagService: AssignmentTagService
) {
    @GetMapping(value = "{id}")
    open fun getAssignmentTag(
            @PathVariable id: UUID): AssignmentTagResultJson {
        val assignmentTag = assignmentTagService.getAssignmentTag(id, assignmentTagService.getProjectIdForAssignmentTag(id))
        return AssignmentTagResultJson(assignmentTag)
    }

    @PostMapping
    open fun createAssignmentTag(
            @RequestBody req: CreateAssignmentTagRequestJson): AssignmentTagResultJson {
        val assignmentTag = assignmentTagService.createAssignmentTag(req.projectId, CreateAssignmentTag(pID = req.projectId, name = req.name, color = req.color))
        return AssignmentTagResultJson(assignmentTag)
    }

    @DeleteMapping(value = "{id}")
    open fun deleteAssignmentTag(
            @PathVariable id: UUID) {
        assignmentTagService.deleteAssignmentTag(id)
    }

    @PutMapping(value = "{id}")
    open fun updateAssignmentTag(
            @PathVariable id: UUID,
            @RequestBody req: UpdateAssignmentRequestJson): AssignmentTagResultJson {
        val assignmentTag = assignmentTagService.updateAssignmentTag(id, UpdateAssignmentTag(name = req.name, color = req.color))
        return AssignmentTagResultJson(assignmentTag)
    }
}