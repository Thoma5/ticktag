package io.ticktag.persistence.user

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.user.entity.User
import java.util.*

@TicktagRepository
interface UserRepository : TicktagCrudRepository<User, UUID> {
    fun findByMailIgnoreCase(mail: String): User?
}
