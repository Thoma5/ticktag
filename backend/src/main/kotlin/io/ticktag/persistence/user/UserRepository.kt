package io.ticktag.persistence.user

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.escapeHqlLike
import io.ticktag.persistence.nullIfEmpty
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
    @Query("select u from User u left join fetch u.image where lower(u.mail) = lower(:mail)")
    fun findByMailIgnoreCase(@Param("mail") mail: String): User?

    @Query("select u from User u left join fetch u.image where u.username = :username")
    fun findByUsername(@Param("username") username: String): User?

    @Query("select u from User u join u.memberships m join m.project p left join fetch u.image where p.id = :projectId")
    fun findInProject(@Param("projectId") projectId: UUID): List<User>

}

interface UserRepositoryCustom {
    fun findByIds(@Param("ids") ids: Collection<UUID>): List<User>

    fun findByProjectIdAndFuzzy(
            projectId: UUID,
            mail: String,
            name: String,
            username: String,
            pageable: Pageable): List<User>

    fun findCreatorsByTicketIds(ids: Collection<UUID>): Map<UUID, User>
}

open class UserRepositoryImpl @Inject constructor(private val em: EntityManager) : UserRepositoryCustom {
    override fun findByIds(ids: Collection<UUID>): List<User> {
        return em.createQuery("select u from User u left join fetch u.image where u.id in :ids", User::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
    }

    override fun findCreatorsByTicketIds(ids: Collection<UUID>): Map<UUID, User> {
        return em.createQuery("select t.id, c from Ticket t join t.createdBy c left join fetch c.image where t.id in :ids", Array<Any>::class.java)
                .setParameter("ids", ids.nullIfEmpty())
                .resultList
                .associateBy({ it[0] as UUID }, { it[1] as User })
    }

    override fun findByProjectIdAndFuzzy(projectId: UUID, mail: String, name: String, username: String, pageable: Pageable): List<User> {
        return em.createQuery("""
            select u
            from User u
            join u.memberships m
            left join fetch u.image
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
