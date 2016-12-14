package io.ticktag.restinterface.ticket.schema

import io.ticktag.restinterface.UpdateNotnullValueJson
import io.ticktag.restinterface.UpdateNullableValueJson
import io.ticktag.service.ticket.dto.UpdateTicket
import java.time.Duration
import java.time.Instant
import java.util.*

data class UpdateTicketRequestJson(
        val title: UpdateNotnullValueJson<String>?,
        val open: UpdateNotnullValueJson<Boolean>?,
        val storyPoints: UpdateNullableValueJson<Int>?,
        val initialEstimatedTime: UpdateNullableValueJson<Duration>?,
        val currentEstimatedTime: UpdateNullableValueJson<Duration>?,
        val dueDate: UpdateNullableValueJson<Instant>?,
        val description: UpdateNotnullValueJson<String>?,
        val parentTicketId: UpdateNullableValueJson<UUID>?
) {
    fun toUpdateTicket() =
            UpdateTicket(
                    title = title?.value,
                    open = open?.value,
                    storyPoints = storyPoints?.value,
                    storyPointsNull = storyPoints != null && storyPoints.value == null,
                    currentEstimatedTime = currentEstimatedTime?.value,
                    currentEstimatedTimeNull = currentEstimatedTime != null && currentEstimatedTime.value == null,
                    initialEstimatedTime = initialEstimatedTime?.value,
                    initialEstimatedTimeNull = initialEstimatedTime != null && initialEstimatedTime.value == null,
                    dueDate = dueDate?.value,
                    dueDateNull = dueDate != null && dueDate.value == null,
                    description = description?.value,
                    parentTicket = parentTicketId?.value,
                    parentTicketNull = parentTicketId != null && parentTicketId.value == null
            )
}
