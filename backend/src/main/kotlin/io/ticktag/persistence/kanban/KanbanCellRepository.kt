package io.ticktag.persistence.kanban

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.kanban.entity.KanbanCell
import io.ticktag.persistence.ticket.entity.Ticket
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Pageable
import java.util.*

@TicktagRepository
interface KanbanCellRepository : TicktagCrudRepository<KanbanCell, UUID> {
    @Query("SELECT t " +
            "FROM KanbanCell k join k.ticket t " +
            "WHERE k.tag.id = :tagId " +
            "ORDER BY k.order")
    fun findByTicketTagId(@Param("tagId") tagId: UUID,pageable: Pageable): List<Ticket>

    fun deleteByTagId(tagId: UUID)
}
