package io.ticktag.persistence.comment

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.Comment
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*


@TicktagRepository
interface CommentRepository : TicktagCrudRepository<Comment, UUID> {
    @Query("SELECT c FROM Comment c join c.ticket t WHERE t.project.id = :projectId and t.descriptionComment.id <> c.id ")
    fun findByTicketProjectId(@Param("projectId") projectId: UUID): List<Comment>

    @Query("select new kotlin.Pair(t.id, c) from Comment c join c.ticket t where t.id in :ids and t.descriptionComment != c")
    fun findNonDescriptionCommentsByTicketIds(@Param("ids") ids: Collection<UUID>): List<Pair<UUID, Comment>>
}