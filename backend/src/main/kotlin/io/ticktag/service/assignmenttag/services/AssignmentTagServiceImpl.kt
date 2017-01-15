package io.ticktag.service.assignmenttag.services

import io.ticktag.TicktagService
import io.ticktag.library.unicode.NameNormalizationLibrary
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.ticket.entity.AssignmentTag
import io.ticktag.service.*
import io.ticktag.service.assignmenttag.AssignmentTagService
import io.ticktag.service.assignmenttag.dto.AssignmentTagResult
import io.ticktag.service.assignmenttag.dto.CreateAssignmentTag
import io.ticktag.service.assignmenttag.dto.UpdateAssignmentTag
import org.springframework.security.access.method.P
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@TicktagService
open class AssignmentTagServiceImpl @Inject constructor(
        private val assignmentTags: AssignmentTagRepository,
        private val projects: ProjectRepository,
        private val nameNormalizationLibrary: NameNormalizationLibrary
) : AssignmentTagService {

    @PreAuthorize(AuthExpr.PROJECT_OBSERVER)
    override fun listAssignmentTags(@P("authProjectId") projectId: UUID): List<AssignmentTagResult> {
        return assignmentTags.findByProjectIdAndDisabled(projectId, false).map(::AssignmentTagResult)
    }

    @PreAuthorize(AuthExpr.READ_ASSIGNMENTTAG)
    override fun getAssignmentTag(@P("authAssignmentTagId") id: UUID): AssignmentTagResult {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        return AssignmentTagResult(assignmentTag)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun createAssignmentTag(@P("authProjectId") projectId: UUID, @Valid createAssignmentTag: CreateAssignmentTag): AssignmentTagResult {
        val project = projects.findOne(createAssignmentTag.projectId) ?: throw NotFoundException()

        val normalizedName = nameNormalizationLibrary.normalize(createAssignmentTag.name)
        if (assignmentTags.findByNormalizedNameAndProjectId(normalizedName, projectId) != null) {
            throw TicktagValidationException(listOf(ValidationError("createAssignmentTag.name", ValidationErrorDetail.Other("inuse"))))
        }

        val assignmentTag = AssignmentTag.create(createAssignmentTag.name, normalizedName, createAssignmentTag.color, project)
        assignmentTags.insert(assignmentTag)
        return AssignmentTagResult(assignmentTag)
    }

    @PreAuthorize(AuthExpr.EDIT_ASSIGNMENTTAG)
    override fun updateAssignmentTag(@P("authAssignmentTagId") id: UUID, @Valid updateAssignmentTag: UpdateAssignmentTag): AssignmentTagResult {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        if (updateAssignmentTag.name != null) {
            val normalizedName = nameNormalizationLibrary.normalize(updateAssignmentTag.name)
            val fountTag = assignmentTags.findByNormalizedNameAndProjectId(normalizedName, assignmentTag.project.id)
            if (fountTag != null && fountTag.id != assignmentTag.id) {
                throw TicktagValidationException(listOf(ValidationError("updateAssignmentTag.name", ValidationErrorDetail.Other("inuse"))))
            }
            assignmentTag.name = updateAssignmentTag.name
            assignmentTag.normalizedName = normalizedName
        }
        if (updateAssignmentTag.color != null) {
            assignmentTag.color = updateAssignmentTag.color
        }
        return AssignmentTagResult(assignmentTag)
    }

    @PreAuthorize(AuthExpr.EDIT_ASSIGNMENTTAG)
    override fun deleteAssignmentTag(@P("authAssignmentTagId") id: UUID) {
        val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
        assignmentTag.normalizedName = ""
        assignmentTag.disabled = true
    }

    /** No server side search
    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun searchAssignmentTags(@P("authProjectId") pid: UUID, name: String): List<AssignmentTagResult> {
    return assignmentTags.findByProjectIdAndNameLikeIgnoreCase(pid, name).map(::AssignmentTagResult)
    }*/

    /** Implement it when needed, makes no sense right now
    @PreAuthorize(AuthExpr.DELETE_ASSIGNMENTTAGS)
    override fun deleteAssignmentTag(id: UUID, @P("authProjectId") projectId: UUID) {
    val assignmentTag = assignmentTags.findOne(id) ?: throw NotFoundException()
    assignmentTags.delete(assignmentTag)
    }**/
}