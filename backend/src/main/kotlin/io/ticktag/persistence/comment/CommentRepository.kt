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
}