package io.ticktag.persistence.comment

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.Comment
import java.util.*


@TicktagRepository
interface CommentRepository : TicktagCrudRepository<Comment, UUID> {
    fun findByTicketProjectId(projectId: UUID): List<Comment>
}