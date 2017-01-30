package io.ticktag.restinterface.ticket.schema

import io.ticktag.restinterface.UpdateNotnullValueJson
import io.ticktag.restinterface.UpdateNullableValueJson
import io.ticktag.restinterface.comment.schema.CommandJson
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
        val parentTicketId: UpdateNullableValueJson<UUID>?,
        val commands: List<CommandJson>?
) {
    fun toUpdateTicket() =
            UpdateTicket(
                    title = title?.toUpdateValue(),
                    open = open?.toUpdateValue(),
                    storyPoints = storyPoints?.toUpdateValue(),
                    currentEstimatedTime = currentEstimatedTime?.toUpdateValue(),
                    initialEstimatedTime = initialEstimatedTime?.toUpdateValue(),
                    dueDate = dueDate?.toUpdateValue(),
                    description = description?.toUpdateValue(),
                    parentTicket = parentTicketId?.toUpdateValue(),
                    commands = commands?.map { it.toCommentCommand() }?.filterNotNull()
            )
}
