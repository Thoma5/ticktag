package  io.ticktag.service.assignmenttag


import io.ticktag.service.assignmenttag.dto.AssignmentTagResult
import io.ticktag.service.assignmenttag.dto.CreateAssignmentTag
import io.ticktag.service.assignmenttag.dto.UpdateAssignmentTag
import java.util.*

interface AssignmentTagService {
    fun createAssignmentTag(projectId: UUID, createAssignmentTag: CreateAssignmentTag): AssignmentTagResult
    fun getAssignmentTag(id: UUID): AssignmentTagResult
    fun updateAssignmentTag(id: UUID, updateAssignmentTag: UpdateAssignmentTag): AssignmentTagResult
    fun listAssignmentTags(projectId: UUID): List<AssignmentTagResult>
    fun deleteAssignmentTag(id: UUID)
}