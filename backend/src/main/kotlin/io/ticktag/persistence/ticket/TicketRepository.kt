package io.ticktag.persistence.ticket.entity

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface TicketRepository : TicktagCrudRepository<Ticket, UUID> {
    fun findByProjectId(projectId: UUID): List<Ticket>

    @Query("Select max(t.number) from Ticket t where project.id = :projectId ")
    fun findNewTicketNumber(@Param("projectId") projectId: UUID): Int?
}
