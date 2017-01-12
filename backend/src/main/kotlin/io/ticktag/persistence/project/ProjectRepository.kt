package io.ticktag.persistence.project

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.nullIfEmpty
import io.ticktag.persistence.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager

@TicktagRepository
interface ProjectRepository : TicktagCrudRepository<Project, UUID>, ProjectRepositoryCustom {
    fun findByNameContainingIgnoreCaseAndDisabledIs(name: String, disabled: Boolean, pageable: Pageable): Page<Project>
    fun findByMembersUserIdAndMembersRoleNotAndNameContainingIgnoreCaseAndDisabledIs(userId: UUID, projectRole: ProjectRole, name: String, disabled: Boolean, pageable: Pageable): Page<Project>
}

interface ProjectRepositoryCustom {
    fun findByTicketIds(ids: Collection<UUID>): Map<UUID, Project>
    fun findByUserIds(ids: Collection<UUID>): Map<UUID, List<Project>>
}

open class ProjectRepositoryImpl @Inject() constructor(private val em: EntityManager) : ProjectRepositoryCustom {
    override fun findByUserIds(ids: Collection<UUID>): Map<UUID, List<Project>> {
        return em.createQuery("select u.id, p from User u join u.memberships m join m.project p where u.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .groupBy({ it[0] as UUID }, { it[1] as Project })
    }

    override fun findByTicketIds(ids: Collection<UUID>): Map<UUID, Project> {
        return em.createQuery("select t.id, p from Ticket t join t.project p where t.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .associateBy({ it[0] as UUID }, { it[1] as Project })
    }
}
