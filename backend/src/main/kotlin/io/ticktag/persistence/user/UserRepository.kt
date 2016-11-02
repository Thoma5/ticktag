package io.ticktag.persistence.user

import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.user.entity.User

interface UserRepository : TicktagCrudRepository<User> {
    fun findByMail(mail: String): User?
}
