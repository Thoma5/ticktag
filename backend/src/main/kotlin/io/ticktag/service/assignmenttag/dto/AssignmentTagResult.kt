package  io.ticktag.service.assignmenttag.dto


import io.ticktag.persistence.ticket.entity.AssignmentTag
import java.util.*

data class AssignmentTagResult(
        val id: UUID,
        val projectId: UUID,
        val name: String,
        val color: String
) {
    constructor(a: AssignmentTag) : this(id = a.id, projectId = a.project.id, name = a.name, color = a.color)
}