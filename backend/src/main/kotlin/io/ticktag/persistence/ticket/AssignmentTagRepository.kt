package  io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.AssignmentTag
import java.util.*

@TicktagRepository
interface AssignmentTagRepository : TicktagCrudRepository<AssignmentTag, UUID> {
    fun findByProjectId(projectId: UUID): List<AssignmentTag>

    //No server side search function
    //fun findByProjectIdAndNameLikeIgnoreCase(projectId: UUID, name: String): List<AssignmentTag>

    fun findByNormalizedNameAndProjectId(normalizedName: String, projectId: UUID): AssignmentTag?
 }