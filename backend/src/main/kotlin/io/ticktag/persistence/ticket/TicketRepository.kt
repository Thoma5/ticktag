package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.escapeHqlLike
import io.ticktag.persistence.nullIfEmpty
import io.ticktag.persistence.orderByClause
import io.ticktag.persistence.ticket.entity.Progress
import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.ticket.entity.TicketTag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager

@TicktagRepository
interface TicketRepository : TicktagCrudRepository<Ticket, UUID>, TicketRepositoryCustom {
    fun findAll(spec: Specification<Ticket>?): List<Ticket>
    fun findAll(spec: Specification<Ticket>?, pageable: Pageable?): Page<Ticket>
    fun findByProjectIdAndNumber(projectId: UUID, number: Int): Ticket?

    @Query("Select max(t.number) from Ticket t where project.id = :projectId ")
    fun findHighestTicketNumberInProject(@Param("projectId") projectId: UUID): Int?

    fun findByNumber(number: Int): Ticket?
}

interface TicketRepositoryCustom {
    fun findByProjectIdAndFuzzy(
            projectId: UUID,
            number: String,
            title: String,
            pageable: Pageable): List<Ticket>

    fun findByIds(@Param("ids") ids: Collection<UUID>): List<Ticket>

    fun findMentionedTickets(@Param("ids") ids: Collection<UUID>): Map<UUID, List<Ticket>>

    fun findMentioningTickets(@Param("ids") ids: Collection<UUID>): Map<UUID, List<Ticket>>

    fun findProgressesByTicketIds(@Param("ids") ids: Collection<UUID>): Map<UUID, Progress>

    fun findSubticketsByTicketIds(@Param("ids") ids: Collection<UUID>): Map<UUID, List<Ticket>>

    fun findParentTicketsByTicketIds(@Param("ids") ids: Collection<UUID>): Map<UUID, Ticket>

    fun findTagsByTicketIds(@Param("ids") ids: Collection<UUID>): Map<UUID, List<TicketTag>>
}

open class TicketRepositoryImpl @Inject constructor(private val em: EntityManager) : TicketRepositoryCustom {
    override fun findByIds(ids: Collection<UUID>): List<Ticket> {
        return em.createQuery("select t from Ticket t where t.id in :ids", Ticket::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
    }

    override fun findTagsByTicketIds(ids: Collection<UUID>): Map<UUID, List<TicketTag>> {
        return em.createQuery("""
            select ti.id, ta from Ticket ti
            join ti.tags ta
            where ti.id in :ids""", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .groupBy( { it[0] as UUID}, { it[1] as TicketTag })
    }

    override fun findMentionedTickets(ids: Collection<UUID>): Map<UUID, List<Ticket>> {
        return em.createQuery("select m.id, t from Ticket t join t.mentioningComments c join c.ticket m where m.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .groupBy( { it[0] as UUID}, { it[1] as Ticket })
    }

    override fun findMentioningTickets(ids: Collection<UUID>): Map<UUID, List<Ticket>> {
        return em.createQuery("select t.id, m from Ticket t join t.mentioningComments c join c.ticket m where t.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .groupBy( { it[0] as UUID}, { it[1] as Ticket })
    }

    override fun findProgressesByTicketIds(ids: Collection<UUID>): Map<UUID, Progress> {
        return em.createQuery("select p.id, p from Progress p where p.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .associateBy( { it[0] as UUID}, { it[1] as Progress })
    }

    override fun findSubticketsByTicketIds(ids: Collection<UUID>): Map<UUID, List<Ticket>> {
        return em.createQuery("select p.id, t from Ticket t join t.parentTicket p where p.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .groupBy( { it[0] as UUID}, { it[1] as Ticket })
    }

    override fun findParentTicketsByTicketIds(ids: Collection<UUID>): Map<UUID, Ticket> {
        return em.createQuery("select s.id, p from Ticket p join p.subTickets s where s.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .associateBy( { it[0] as UUID}, { it[1] as Ticket })
    }

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
