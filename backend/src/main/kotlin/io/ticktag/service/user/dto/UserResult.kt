package io.ticktag.service.user.dto

import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import java.util.*

data class UserResult(
        val id: UUID,
        val mail: String?,
        val username: String,
        val name: String,
        val currentToken: UUID,
        val role: Role,
        val profile_pic: ByteArray?
) {
    constructor(u: User) : this(id = u.id, mail = u.mail, name = u.name, currentToken = u.currentToken, role = u.role, profile_pic = u.profilePic, username = u.username)
}
