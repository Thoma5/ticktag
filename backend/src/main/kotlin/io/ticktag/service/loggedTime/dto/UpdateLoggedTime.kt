package io.ticktag.service.loggedTime.dto

import java.time.Duration
import java.util.*

/**
 * Created by stefandraskovits on 23/11/2016.
 */
data class UpdateLoggedTime (val time: Duration?,
                        val categoryId: UUID?){
}