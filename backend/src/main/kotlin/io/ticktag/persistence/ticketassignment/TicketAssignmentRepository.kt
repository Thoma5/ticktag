package io.ticktag.persistence.ticketassignment

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.nullIfEmpty
import io.ticktag.persistence.ticket.entity.AssignedTicketUser
import io.ticktag.persistence.ticket.entity.AssignedTicketUserKey
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager

@TicktagRepository
interface TicketAssignmentRepository : TicktagCrudRepository<AssignedTicketUser, AssignedTicketUserKey>, TicketAssignmentRepositoryCustom {
    fun findByUserIdAndTicketId(userId: UUID, ticketId: UUID): List<AssignedTicketUser>
    fun deleteByUserIdAndTicketId(userId: UUID, ticketId: UUID)
}

interface TicketAssignmentRepositoryCustom {
    fun findByTicketIds(ids: Collection<UUID>): Map<UUID, List<AssignedTicketUser>>
}

open class TicketAssignmentRepositoryImpl @Inject constructor(private val em: EntityManager) : TicketAssignmentRepositoryCustom {
    override fun findByTicketIds(ids: Collection<UUID>): Map<UUID, List<AssignedTicketUser>> {
        return em.createQuery(
                """select ti.id, a from AssignedTicketUser a
                    join fetch a.tag ta
                    join fetch a.ticket ti
                    join fetch a.user u
                        left join fetch u.image
                    where ti.id in :ids
                """, Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .groupBy({ it[0] as UUID }, { it[1] as AssignedTicketUser })
    }
}
