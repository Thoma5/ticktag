package  io.ticktag.service.assignmenttag.services


import io.ticktag.service.assignmenttag.dto.*
import java.util.*

interface AssignmentTagService {

    fun createAssignmentTag(projectID: UUID, assignmentTag: CreateAssignmentTag): AssignmentTagResult
    fun getAssignmentTag(id: UUID): AssignmentTagResult
    fun updateAssignmentTag(id: UUID, assignmentTag: UpdateAssignmentTag): AssignmentTagResult
    fun listAssignmentTags(pid: UUID): List<AssignmentTagResult>
    //fun searchAssignmentTags(pid: UUID, name: String): List<AssignmentTagResult> //No need
}