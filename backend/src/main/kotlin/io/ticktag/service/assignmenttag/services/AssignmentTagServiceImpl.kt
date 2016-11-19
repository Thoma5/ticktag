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

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun getAssignmentTag(id: UUID, @P("authProjectId") projectId: UUID): AssignmentTagResult {
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

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun updateAssignmentTag(id: UUID, @P("authProjectId") projectId: UUID, @Valid updateAssignmentTag: UpdateAssignmentTag): AssignmentTagResult {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        if (updateAssignmentTag.name != null) {
            assignmentTag.name = updateAssignmentTag.name
        }
        if (updateAssignmentTag.color != null) {
            assignmentTag.color = updateAssignmentTag.color
        }
        return AssignmentTagResult(assignmentTag)
    }

    @PreAuthorize(AuthExpr.PROJECT_ADMIN)
    override fun deleteAssignmentTag(id: UUID, @P("authProjectId") projectId: UUID) {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        assignmentTags.delete(assignmentTag)
    }

    override fun getProjectIdForAssignmentTag(id: UUID): UUID {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        return assignmentTag.project.id
    }

}