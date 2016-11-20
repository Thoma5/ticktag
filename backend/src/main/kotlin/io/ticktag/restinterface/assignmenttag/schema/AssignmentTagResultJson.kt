package  io.ticktag.restinterface.assignmenttag.schema

import io.ticktag.service.assignmenttag.dto.AssignmentTagResult
import java.util.*

data class AssignmentTagResultJson(
        val id: UUID,
        val projectId: UUID,
        val name: String,
        val color: String
) {
    constructor(a: AssignmentTagResult) : this(id = a.id, projectId = a.projectId, name = a.name, color = a.color)
}