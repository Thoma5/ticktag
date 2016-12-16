package io.ticktag.service.loggedtime.dto


import io.ticktag.restinterface.loggedtime.schema.CreateLoggedTimeJson
import io.ticktag.util.PositiveDuration
import java.time.Duration
import java.util.*


data class CreateLoggedTime(
        @field:PositiveDuration val time: Duration,
        val categoryId: UUID
) {
    constructor(l: CreateLoggedTimeJson) : this(time = l.time, categoryId = l.categoryId)


}