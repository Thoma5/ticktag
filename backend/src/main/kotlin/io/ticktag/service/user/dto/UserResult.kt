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
        val imageId: TempImageId
) {
    constructor(u: User, imageId: TempImageId) : this(id = u.id, mail = u.mail, name = u.name, currentToken = u.currentToken, role = u.role, username = u.username, imageId = imageId)
}
