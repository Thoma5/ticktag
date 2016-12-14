package io.ticktag.service.ticket.dto

import io.ticktag.persistence.ticket.entity.Progress
import java.time.Duration

data class ProgressResult(
        val loggedTime: Duration?,
        val initalEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val initialProgress: Float?,
        val progress: Float?
) {
    constructor(p: Progress?) : this(loggedTime = p?.totalLoggedTime, initalEstimatedTime = p?.totalInitialEstimatedTime, currentEstimatedTime = p?.currentEstimatedTime, initialProgress = p?.totalInitialProgress, progress = p?.totalProgress)
}