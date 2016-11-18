package io.ticktag.service.project.dto

import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.Size

data class CreateTicket(
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