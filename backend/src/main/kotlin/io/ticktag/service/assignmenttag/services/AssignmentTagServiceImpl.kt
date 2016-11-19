package io.ticktag.service.assignmenttag.services

import io.ticktag.TicktagService
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.entity.AssignmentTag
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.assignmenttag.dto.*
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class AssignmentTagServiceImpl @Inject constructor(
        private val assignmentTags: AssignmentTagRepository,
        private val projects: ProjectRepository
) : AssignmentTagService {

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun searchAssignmentTags(@P("authProjectId") pid: UUID, name: String): List<AssignmentTagResult> {
        return assignmentTags.findByProjectIdAndNameLikeIgnoreCase(pid, name).map(::AssignmentTagResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun listAssignmentTags(@P("authProjectId") pid: UUID): List<AssignmentTagResult> {
        return assignmentTags.findByProjectId(pid).map(::AssignmentTagResult)
    }

    @PreAuthorize(AuthExpr.READ_ASSIGNMENTTAG)
    override fun getAssignmentTag(@P("authAssignmentTagId") id: UUID): AssignmentTagResult {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        return AssignmentTagResult(assignmentTag)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun createAssignmentTag(@P("authProjectId") projectId: UUID, @Valid assignmentTag: CreateAssignmentTag): AssignmentTagResult {
        val project = projects.findOne(assignmentTag.pID) ?: throw NotFoundException()
        val assignmentTag = AssignmentTag.create(assignmentTag.name, assignmentTag.color, project)
        assignmentTags.insert(assignmentTag)
        return AssignmentTagResult(assignmentTag)
    }

    @PreAuthorize(AuthExpr.EDIT_ASSIGNMENTTAG)
    override fun updateAssignmentTag(@P("authAssignmentTagId") id: UUID, @Valid updateAssignmentTag: UpdateAssignmentTag): AssignmentTagResult {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        if (updateAssignmentTag.name != null) {
            assignmentTag.name = updateAssignmentTag.name
        }
        if (updateAssignmentTag.color != null) {
            assignmentTag.color = updateAssignmentTag.color
        }
        return AssignmentTagResult(assignmentTag)
    }

    /** Implement it when needed, makes no sense right now
    @PreAuthorize(AuthExpr.DELETE_ASSIGNMENTTAGS)
    override fun deleteAssignmentTag(id: UUID, @P("authProjectId") projectId: UUID) {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        assignmentTags.delete(assignmentTag)
    }**/
}