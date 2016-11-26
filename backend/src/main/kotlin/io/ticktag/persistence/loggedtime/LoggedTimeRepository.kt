package io.ticktag.persistence.loggedtime

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.LoggedTime
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface LoggedTimeRepository : TicktagCrudRepository<LoggedTime, UUID> {
    @Query("SELECT l " +
            "FROM LoggedTime l join l.comment c join c.ticket t  " +
            "WHERE (coalesce(:projectId,null) is null OR c.user.id = :userId) " +
            "AND (coalesce(:userId ,null) is null OR t.project.id = :projectId) " +
            "AND (coalesce(:categoryId,null) is null  OR l.category.id = :categoryId)")
    fun findByProjectIdOrUserIdOrCategoryId(@Param("projectId") projectId: UUID?, @Param("userId") userId: UUID?, @Param("categoryId") categoryId: UUID?): List<LoggedTime>
}