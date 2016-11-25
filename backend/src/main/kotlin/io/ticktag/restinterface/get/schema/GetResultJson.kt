package io.ticktag.restinterface.get.schema

import io.ticktag.restinterface.user.schema.UserResultJson
import java.util.*

data class GetResultJson(
        val users: Map<UUID, UserResultJson>
)
