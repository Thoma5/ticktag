package io.ticktag.persistence.LoggedTime

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.LoggedTime
import java.util.*

@TicktagRepository
interface LoggedTimeRepository : TicktagCrudRepository<LoggedTime, UUID> {

}