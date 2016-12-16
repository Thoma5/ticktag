package io.ticktag.persistence.loggedtime

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.nullIfEmpty
import io.ticktag.persistence.ticket.entity.LoggedTime
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager

@TicktagRepository
interface LoggedTimeRepository : TicktagCrudRepository<LoggedTime, UUID>, LoggedTimeRepositoryCustom {
    @Query("SELECT l " +
            "FROM LoggedTime l join l.comment c join c.ticket t  " +
            "WHERE (coalesce(:projectId,null) is null OR c.user.id = :userId) " +
            "AND (coalesce(:userId ,null) is null OR t.project.id = :projectId) " +
            "AND (coalesce(:categoryId,null) is null  OR l.category.id = :categoryId)")
    fun findByProjectIdOrUserIdOrCategoryId(@Param("projectId") projectId: UUID?, @Param("userId") userId: UUID?, @Param("categoryId") categoryId: UUID?): List<LoggedTime>
}

interface LoggedTimeRepositoryCustom {
    fun findByIds(@Param("ids") ids: Collection<UUID>): List<LoggedTime>
}

open class LoggedTimeRepositoryImpl @Inject() constructor(private val em: EntityManager): LoggedTimeRepositoryCustom {
    override fun findByIds(ids: Collection<UUID>): List<LoggedTime> {
        return em.createQuery("select t from LoggedTime t where t.id in :ids", LoggedTime::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
    }
}