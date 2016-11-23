package io.ticktag.restinterface.loggedTime.schema

import java.time.Duration
import java.util.*

/**
 * Created by stefandraskovits on 23/11/2016.
 */
data class UpdateLoggedTimeJson (
        val time: Duration?,
        val categoryId: UUID?
){
}