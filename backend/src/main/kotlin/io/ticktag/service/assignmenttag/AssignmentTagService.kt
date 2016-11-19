package  io.ticktag.service.assignmenttag.services


import io.ticktag.service.assignmenttag.dto.*
import java.util.*

interface AssignmentTagService {

    fun createAssignmentTag(projectId: UUID, assignmentTag: CreateAssignmentTag): AssignmentTagResult
    fun getAssignmentTag(id: UUID, projectId: UUID): AssignmentTagResult
    fun updateAssignmentTag(id: UUID, projectId: UUID, assignmentTag: UpdateAssignmentTag): AssignmentTagResult
    fun deleteAssignmentTag(id: UUID, projectId: UUID)
    fun  getProjectIdForAssignmentTag(id: UUID): UUID
}