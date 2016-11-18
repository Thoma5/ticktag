package io.ticktag.restinterface.user.schema

import io.ticktag.persistence.user.entity.Role
import java.time.Duration
import java.time.Instant
import java.util.*

data class CreateTicketRequestJson(
        val number:Int,
        val createTime: Instant,
        val title: String,
        val open:Boolean,
        val storyPoints:Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String,
        val projectID: UUID
)
