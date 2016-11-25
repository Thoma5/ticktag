package io.ticktag.restinterface.loggedTime.schema

import java.time.Duration
import java.util.*

data class UpdateLoggedTimeJson(
        val time: Duration?,
        val categoryId: UUID?
)