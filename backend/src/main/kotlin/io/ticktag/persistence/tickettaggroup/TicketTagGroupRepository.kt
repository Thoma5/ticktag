package io.ticktag.persistence.tickettaggroup

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketTagGroup
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface TicketTagGroupRepository : TicktagCrudRepository<TicketTagGroup, UUID> {
    @Query("SELECT g from TicketTagGroup g " +
            "WHERE g.project.id = :projectId AND g.exclusive = true")
    fun findExclusiveTicketTagGroupsByProjectId(@Param("projectId") projectId: UUID) : List<TicketTagGroup>
}
