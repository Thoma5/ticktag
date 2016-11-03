package io.ticktag.restinterface.auth.schema

import java.util.*

data class WhoamiResultJson(
        val id: UUID,
        val authorities: List<String>
)
