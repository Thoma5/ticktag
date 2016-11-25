package io.ticktag.service.loggedtime.dto

import java.time.Duration
import java.util.*

data class CreateLoggedTimeJson(
        val time: Duration,
        val commentId: UUID?,
        val categoryId: UUID
)