package io.ticktag.service.loggedtime.dto

import java.time.Duration
import java.util.*

data class UpdateLoggedTime(val time: Duration?,
                            val categoryId: UUID?)