package  io.ticktag.service.assignmenttag


import io.ticktag.service.assignmenttag.dto.AssignmentTagResult
import io.ticktag.service.assignmenttag.dto.CreateAssignmentTag
import io.ticktag.service.assignmenttag.dto.UpdateAssignmentTag
import java.util.*

interface AssignmentTagService {
    /**
     * create an assignment Tag and store it into the database.
     * An assignment Tag can be used to tag an assignment between a user and a ticket
     * @param projectId id of the project where the tag will be stored
     * @param createAssignmentTag DTO which encapsulates the properties of an assignmentTag
     * @return the created assignmentTag encapsulated in an AssignmentTagResult
     */
    fun createAssignmentTag(projectId: UUID, createAssignmentTag: CreateAssignmentTag): AssignmentTagResult

    /**
     * Find a specific Assignment Tag
     * @param id id of the object to find - if this is id is not available there will be an exception.
     */
    fun getAssignmentTag(id: UUID): AssignmentTagResult
    /**
     * Update a specific Assignment Tag
     * @param id id of the object to updated - if this is id is not available there will be an exception.
     * @param updateAssignmentTag properties of an AssignmentTag encapsulated
     */
    fun updateAssignmentTag(id: UUID, updateAssignmentTag: UpdateAssignmentTag): AssignmentTagResult

    /**
     * retuns a list of all AssignmentTags in a project
     */
    fun listAssignmentTags(projectId: UUID): List<AssignmentTagResult>

    /**
     * diables a assignmentTag so it can't be used anymore
     */
    fun deleteAssignmentTag(id: UUID)
}