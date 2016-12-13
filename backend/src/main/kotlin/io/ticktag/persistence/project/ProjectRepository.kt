package io.ticktag.persistence.project

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager

@TicktagRepository
interface ProjectRepository : TicktagCrudRepository<Project, UUID>, ProjectRepositoryCustom {
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Project>
    fun findByMembersUserIdAndNameContainingIgnoreCase(userId: UUID, name: String, pageable: Pageable): Page<Project>
    fun countByMembersUserId(userId: UUID): Int
}

interface ProjectRepositoryCustom {
    fun findByTicketIds(ids: Collection<UUID>): List<Pair<UUID, Project>>
}

open class ProjectRepositoryImpl @Inject() constructor(private val em: EntityManager) : ProjectRepositoryCustom {
    override fun findByTicketIds(ids: Collection<UUID>): List<Pair<UUID, Project>> {
        return em.createQuery("select t.id, p from Ticket t join t.project p where t.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids)
                .resultList
                .map { Pair(it[0] as UUID, it[1] as Project) }
    }
}
