package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.Ticket
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface TicketRepository : TicktagCrudRepository<Ticket, UUID> {
    fun findByProjectId(projectId: UUID,pageable: Pageable): Page<Ticket>

    @Query("Select max(t.number) from Ticket t where project.id = :projectId ")
    fun findHighestTicketNumberInProject(@Param("projectId") projectId: UUID): Int?

    @Query("""
        select t
        from Ticket t
        where t.project.id = :projectId
        and (
            cast(t.number as string) like :number
            or upper(t.title) like upper(:title)
        )
    """)
    fun findByProjectIdAndFuzzy(
            @Param("projectId") projectId: UUID,
            @Param("number") numberLike: String,
            @Param("title") titleLike: String,
            pageable: Pageable): List<Ticket>

    @Query("select t from Ticket t where t.id in :ids")
    fun findByIds(@Param("ids") ids: Collection<UUID>): List<Ticket>

    fun findByNumber(number: Int): Ticket?
}