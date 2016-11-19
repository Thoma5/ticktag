package  io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.AssignmentTag
import io.ticktag.persistence.ticket.entity.Comment
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface AssignmentTagRepository : TicktagCrudRepository<AssignmentTag, UUID> {
    fun findByProjectId(projectId: UUID): List<AssignmentTag>

    fun findByProjectIdAndNameLikeIgnoreCase(projectId: UUID, name: String): List<AssignmentTag>
 }