package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.escapeHqlLike
import io.ticktag.persistence.orderByClause
import io.ticktag.persistence.ticket.entity.Ticket
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager

@TicktagRepository
interface TicketRepository : TicktagCrudRepository<Ticket, UUID>, TicketRepositoryCustom {
    fun findByProjectId(projectId: UUID,pageable: Pageable): Page<Ticket>

    @Query("Select max(t.number) from Ticket t where project.id = :projectId ")
    fun findHighestTicketNumberInProject(@Param("projectId") projectId: UUID): Int?

    @Query("select t from Ticket t where t.id in :ids")
    fun findByIds(@Param("ids") ids: Collection<UUID>): List<Ticket>

    fun findByNumber(number: Int): Ticket?
}

interface TicketRepositoryCustom {
    fun findByProjectIdAndFuzzy(
            projectId: UUID,
            number: String,
            title: String,
            pageable: Pageable): List<Ticket>
}

open class TicketRepositoryImpl @Inject constructor(private val em: EntityManager) : TicketRepositoryCustom {
    override fun findByProjectIdAndFuzzy(projectId: UUID, number: String, title: String, pageable: Pageable): List<Ticket> {
        return em.createQuery("""
            select t
            from Ticket t
            where t.project.id = :project
            and (
                upper(cast(t.number as string)) like '%'||upper(:number)||'%' escape '!'
                or upper(t.title) like '%'||upper(:title)||'%' escape '!'
            )
            ${pageable.orderByClause()}
        """, Ticket::class.java)
                .setParameter("project", projectId)
                .setParameter("number", number.escapeHqlLike('!'))
                .setParameter("title", title.escapeHqlLike('!'))
                .setFirstResult(pageable.offset)
                .setMaxResults(pageable.pageSize)
                .resultList
    }
}
