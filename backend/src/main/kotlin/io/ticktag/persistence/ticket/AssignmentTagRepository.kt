package  io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.AssignmentTag
import java.util.*

@TicktagRepository
interface AssignmentTagRepository : TicktagCrudRepository<AssignmentTag, UUID> {

}