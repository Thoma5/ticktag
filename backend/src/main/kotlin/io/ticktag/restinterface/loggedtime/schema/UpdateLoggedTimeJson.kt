package io.ticktag.restinterface.loggedtime.schema

import java.time.Duration
import java.util.*

data class UpdateLoggedTimeJson(
        val time: Duration?,
        val categoryId: UUID?
)