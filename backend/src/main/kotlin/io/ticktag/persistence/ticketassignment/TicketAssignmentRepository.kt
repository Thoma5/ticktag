package io.ticktag.persistence.ticket.entity

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

@TicktagRepository
interface TicketAssignmentRepository : TicktagCrudRepository<AssignedTicketUser, AssignedTicketUserKey> {
    fun findByUserId(userId: UUID, pageable: Pageable): Page<AssignedTicketUser>

}
