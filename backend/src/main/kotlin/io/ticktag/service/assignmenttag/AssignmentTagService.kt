package  io.ticktag.service.assignmenttag.services


import io.ticktag.service.assignmenttag.dto.AssignmentTagResult
import io.ticktag.service.assignmenttag.dto.CreateAssignmentTag
import io.ticktag.service.assignmenttag.dto.UpdateAssignmentTag
import java.util.*

interface AssignmentTagService {

    fun createAssignmentTag(projectID: UUID, assignmentTag: CreateAssignmentTag): AssignmentTagResult
    fun getAssignmentTag(id: UUID): AssignmentTagResult
    fun updateAssignmentTag(id: UUID, assignmentTag: UpdateAssignmentTag): AssignmentTagResult
    fun listAssignmentTags(projectId: UUID): List<AssignmentTagResult>
    fun searchAssignmentTags(projectId: UUID, name: String): List<AssignmentTagResult>
}