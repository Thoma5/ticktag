package io.ticktag.persistence.ticket

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.dto.TicketFilter
import io.ticktag.persistence.ticket.entity.Ticket
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager


@TicktagRepository
interface TicketRepository : TicktagCrudRepository<Ticket, UUID>, TicketRepositoryCustom {

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

interface TicketRepositoryCustom {
    fun findAll(ticketFilter: TicketFilter, pageable: Pageable): Page<Ticket>

}

open class TicketRepositoryImpl @Inject constructor(
        private val em: EntityManager
) : TicketRepositoryCustom {


    override fun findAll(ticketFilter: TicketFilter, pageable: Pageable): Page<Ticket> {

        val q = em.createQuery("""select t
from Ticket t
left join t.tags tag
left join t.assignedTicketUsers atu
left join atu.user u
where t.project.id = :projectId
and (:tagsSize = 0L or tag.normalizedName in :tags or tag.normalizedName is null)
and (:usersSize = 0L or u.username in :users or u.username is null)
group by t
having (:tagsSize = 0L or count(distinct tag.normalizedName) = :tagsSize)
and (:usersSize = 0L or count(distinct u.username) = :usersSize)
order by ${pageable.sort.joinToString{s -> s.property+ ' ' + s.direction.toString()}}
""", Ticket::class.java)
                .setParameter("projectId", ticketFilter.project)
                .setParameter("tags", ticketFilter.tags)
                .setParameter("tagsSize", (ticketFilter.tags?.size?:0).toLong())
                .setParameter("users",ticketFilter.users)
                .setParameter("usersSize",( ticketFilter.users?.size?:0).toLong())
                .setFirstResult(pageable.offset)
                .setMaxResults(pageable.pageSize)
        val rl = q.resultList
        return PageImpl(rl, pageable, rl.size.toLong())
        /*       val cb = em.criteriaBuilder
               val c = cb.createQuery(Ticket::class.java)
               val root = c.from(Ticket::class.java)
               val predicates = mutableListOf<Predicate>()

               predicates.add(cb.equal(root.get<Project>("project").get<UUID>("id"), ticketFilter.project))

                   if (number != null) {
                       c.where(cb.equal(root.get<Int>("number"), number))
                   }
                   if (title != null) {
                       c.where(cb.like(cb.lower(root.get<String>("title")), "%"+title.toLowerCase()+"%"))
                   }
                   if (ticketFilter.tags != null) {
                     val dc = c.subquery(TicketTag::class.java)
                               .correlate(root)
                               .get(root.join<List<Ticket>>("tags"))
                               //.join<Ticket, TicketTag>("tags", JoinType.INNER)
                               .get<String>("normalizedName")
                       for (tag in  ticketFilter.tags){
                           predicates.add(cb.`in`(cb.literal(tag)).value(dc)) //.(Subqueries.`in`(cb.literal(tag),dc))
                       }


                   }

              if (users != null){
                   predicates.add(cb.isTrue((root.get<List<String>>("assignedTicketUsers")).`in`(users)))
               }
                   if (progressOne != null) {
                       if (progressTwo != null) {
                           c.where(cb.between(root.get<Progress>("progress").get<Float>("progress"), progressOne, progressTwo))
                       } else if (progressGreater != null) {
                           if (progressGreater == true) {
                               c.where(cb.greaterThan(root.get<Progress>("progress").get<Float>("progress"), progressOne))
                           } else {
                               c.where(cb.lessThanOrEqualTo(root.get<Progress>("progress").get<Float>("progress"), progressOne))
                           }
                       } else {
                           c.where(cb.equal(root.get<Progress>("progress").get<List<String>>("progress"), progressOne))
                       }
                   }
                   if (dueDateFrom != null && dueDateTwo != null) {
                       predicates.add(cb.between(root.get<Instant>("dueDate"), dueDateFrom, dueDateTwo))
                   } else if (dueDateFrom != null && dueDateTwo == null && dueDateGreater != null) {
                       if (dueDateGreater == true) {
                           c.where(cb.greaterThan(root.get<Instant>("dueDate"), dueDateFrom))
                       } else {
                           c.where(cb.lessThanOrEqualTo(root.get<Instant>("dueDate"), dueDateFrom))
                       }
                   }
                   if (open != null) {
                       c.where(cb.equal(root.get<Boolean>("open"), open))
                   }
                   return if (predicates.size <= 0) {
                       null
                   } else {
                       cb.and(*predicates.toTypedArray())


               }
               c.where(*predicates.toTypedArray())
               val resultList =em.createQuery(c).resultList
               return PageImpl(resultList, pageable, resultList.size.toLong())*/
    }

}