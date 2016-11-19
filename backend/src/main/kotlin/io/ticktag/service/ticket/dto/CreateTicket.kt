package io.ticktag.service.project.dto

import io.ticktag.restinterface.user.schema.CreateTicketRequestJson
import java.time.Duration
import java.time.Instant
import java.util.*

import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Size

data class CreateTicket(
        @field:Size(max = 100) val title: String,
        val open: Boolean,
        @field:DecimalMin("0") val storyPoints: Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        @field:Size(min = 1, max = 5000) val description: String,
        val projectID: UUID,
        val subTickets: List<CreateTicket>?,
        val existingSubTicketIds: List<UUID>?,
        var partenTicket: UUID?
) {
    constructor(req: CreateTicketRequestJson) : this(
            req.title, req.open, req.storyPoints, req.initialEstimatedTime,
            req.currentEstimatedTime, req.dueDate, req.description, req.projectId, req.subTickets?.map { s -> CreateTicket(s) }, req.existingSubTicketIds, req.partenTicket)


}