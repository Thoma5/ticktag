package io.ticktag.persistence.user

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.user.entity.User

@TicktagRepository
interface UserRepository : TicktagCrudRepository<User> {
    fun findByMailIgnoreCase(mail: String): User?


}
