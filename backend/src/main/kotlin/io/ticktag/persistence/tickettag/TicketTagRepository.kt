package io.ticktag.persistence.tickettag

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TicketTag
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface TicketTagRepository : TicktagCrudRepository<TicketTag, UUID> {

    @Query("SELECT t from TicketTag t " +
            "JOIN t.ticketTagGroup g JOIN g.project p " +
            "WHERE t.name = :name AND p.id = :projectId")
    fun findByNameAndProjectId(@Param("name") name: String, @Param("projectId") projectId: UUID) : TicketTag?
}