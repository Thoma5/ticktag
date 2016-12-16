package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.user.entity.Role
import io.ticktag.service.user.dto.UserResult
import org.apache.commons.codec.binary.Base64
import java.util.*

data class UserResultJson(
        val id: UUID,
        val name: String,
        val username: String,
        val mail: String?,
        val role: Role,
        val imageId: String
) {
    constructor(u: UserResult) : this(id = u.id, name = u.name, mail = u.mail, role = u.role, username = u.username, imageId = Base64.encodeBase64URLSafeString(u.imageId.data))
}