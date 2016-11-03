package io.ticktag.restinterface.user.schema

import io.ticktag.service.user.dto.UserResult
import java.util.*

data class UserResultJson(
        val id: UUID,
        val name: String,
        val mail: String
) {
    constructor(u: UserResult) : this(id = u.id, name = u.name, mail = u.mail)
}