package io.ticktag.service.user.dto

import io.ticktag.persistence.user.entity.User
import java.util.*

data class UserResult(
        val id: UUID,
        val mail: String,
        val name: String,
        val currentToken: UUID
) {
    constructor(u: User) : this(id = u.id, mail = u.mail, name = u.name, currentToken = u.currentToken)
}
