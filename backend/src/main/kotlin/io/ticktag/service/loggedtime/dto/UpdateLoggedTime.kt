package io.ticktag.service.loggedtime.dto

import io.ticktag.util.PositiveDuration
import java.time.Duration
import java.util.*


data class UpdateLoggedTime(@field:PositiveDuration val time: Duration?,
                            val categoryId: UUID?)