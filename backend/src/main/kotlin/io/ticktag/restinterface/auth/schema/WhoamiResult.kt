package io.ticktag.restinterface.auth.schema

import java.util.*

data class WhoamiResult(
        val id: UUID,
        val authorities: List<String>
) {}
