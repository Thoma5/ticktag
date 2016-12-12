package io.ticktag.restinterface.ticket.schema

import io.ticktag.service.ticket.dto.UpdateTicket
import java.time.Duration
import java.time.Instant
import java.util.*

// This distinction is made for Jackson and the TypeScript code generator
data class UpdateTicketRequestNullableValueJson<out T>(val value: T?)
data class UpdateTicketRequestNotnullValueJson<out T>(val value: T)

data class UpdateTicketRequestJson(
        val title: UpdateTicketRequestNotnullValueJson<String>?,
        val open: UpdateTicketRequestNotnullValueJson<Boolean>?,
        val storyPoints: UpdateTicketRequestNullableValueJson<Int>?,
        val initialEstimatedTime: UpdateTicketRequestNullableValueJson<Duration>?,
        val currentEstimatedTime: UpdateTicketRequestNullableValueJson<Duration>?,
        val dueDate: UpdateTicketRequestNullableValueJson<Instant>?,
        val description: UpdateTicketRequestNotnullValueJson<String>?,
        val parentTicketId: UpdateTicketRequestNullableValueJson<UUID>?
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
