package io.ticktag.persistence.comment

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.Comment
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager


@TicktagRepository
interface CommentRepository : TicktagCrudRepository<Comment, UUID>, CommentRepositoryCustom {
    @Query("SELECT c FROM Comment c join c.ticket t WHERE t.project.id = :projectId and t.descriptionComment.id <> c.id ")
    fun findByTicketProjectId(@Param("projectId") projectId: UUID): List<Comment>
}

interface CommentRepositoryCustom {
    fun findNonDescriptionCommentsByTicketIds(@Param("ids") ids: Collection<UUID>): List<Pair<UUID, Comment>>
    fun findDescriptionCommentsByTicketIds(@Param("ids") ids: Collection<UUID>): List<Pair<UUID, Comment>>
}

open class CommentRepositoryImpl @Inject constructor(private val em: EntityManager) : CommentRepositoryCustom {
    override fun findNonDescriptionCommentsByTicketIds(@Param("ids") ids: Collection<UUID>): List<Pair<UUID, Comment>> {
        return em.createQuery("select t.id, c from Comment c join c.ticket t left join fetch c.describedTicket where t.id in :ids and t.descriptionComment <> c", Array<Any>::class.java)
                .setParameter("ids", ids)
                .resultList
                .map { Pair(it[0] as UUID, it[1] as Comment) }
    }

    override fun findDescriptionCommentsByTicketIds(ids: Collection<UUID>): List<Pair<UUID, Comment>> {
        return em.createQuery("select t.id, c from Comment c join fetch c.describedTicket t where t.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids)
                .resultList
                .map { Pair(it[0] as UUID, it[1] as Comment) }
    }
}
