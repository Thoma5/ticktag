package io.ticktag.persistence.user

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.escapeHqlLike
import io.ticktag.persistence.orderByClause
import io.ticktag.persistence.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManager

@TicktagRepository
interface UserRepository : TicktagCrudRepository<User, UUID>, UserRepositoryCustom {
    fun findByMailIgnoreCase(mail: String): User?

    fun findByUsername(username: String): User?

    @Query("select u from User u join fetch u.memberships m join fetch m.project where u.id = :id")
    fun findOneWithProjects(@Param("id") id: UUID): User?

    @Query("select u from User u where u.id in :ids")
    fun findByIds(@Param("ids") ids: Collection<UUID>): List<User>
}

interface UserRepositoryCustom {
    fun findByProjectIdAndFuzzy(
            projectId: UUID,
            mail: String,
            name: String,
            username: String,
            pageable: Pageable): List<User>

    fun findCreatorsByTicketIds(ids: Collection<UUID>): Map<UUID, User>
}

open class UserRepositoryImpl @Inject constructor(private val em: EntityManager) : UserRepositoryCustom {
    override fun findCreatorsByTicketIds(ids: Collection<UUID>): Map<UUID, User> {
        return em.createQuery("select t.id, c from Ticket t join t.createdBy c left join fetch c.image where t.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids)
                .resultList
                .associateBy({ it[0] as UUID }, { it[1] as User })
    }

    override fun findByProjectIdAndFuzzy(projectId: UUID, mail: String, name: String, username: String, pageable: Pageable): List<User> {
        return em.createQuery("""
            select u
            from User u
            join u.memberships m
            where m.project.id = :project
            and (
                upper(mail) like '%'||upper(:mail)||'%' escape '!'
                or upper(name) like '%'||upper(:name)||'%' escape '!'
                or upper(username) like '%'||upper(:username)||'%' escape '!'
            )
            ${pageable.orderByClause()}
        """, User::class.java)
                .setParameter("project", projectId)
                .setParameter("mail", mail.escapeHqlLike('!'))
                .setParameter("name", name.escapeHqlLike('!'))
                .setParameter("username", username.escapeHqlLike('!'))
                .setFirstResult(pageable.offset)
                .setMaxResults(pageable.pageSize)
                .resultList
    }
}
