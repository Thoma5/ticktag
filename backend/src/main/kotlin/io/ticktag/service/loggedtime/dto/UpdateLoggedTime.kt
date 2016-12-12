package io.ticktag.service.loggedtime.dto

import io.ticktag.util.CheckDuration
import java.time.Duration
import java.util.*


data class UpdateLoggedTime(@field:CheckDuration val time: Duration?,
                            val categoryId: UUID?) {

}