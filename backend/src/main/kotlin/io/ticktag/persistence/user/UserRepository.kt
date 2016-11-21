package io.ticktag.persistence.user

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.user.entity.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface UserRepository : TicktagCrudRepository<User, UUID> {
    fun findByMailIgnoreCase(mail: String): User?

    fun findByUsername(username: String): User?

    @Query("select u from User u join fetch u.memberships m join fetch m.project where u.id = :id")
    fun findOneWithProjects(@Param("id") id: UUID): User?
}
