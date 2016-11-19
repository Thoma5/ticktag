package io.ticktag.service.project.dto

import io.ticktag.restinterface.user.schema.UpdateTicketRequestJson
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.Size
import javax.validation.constraints.DecimalMin

data class UpdateTicket(
        @field:Size(max = 100) val title: String?,
        val open: Boolean?,
        @field:DecimalMin("0") val storyPoints: Int?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        @field:Size(min = 1, max = 5000) val description: String?,
        val subTickets: List<CreateTicket>?,
        val existingSubTicketIds: List<UUID>?,
        val partenTicket: UUID?

) {
    constructor(req: UpdateTicketRequestJson) : this(
            req.title, req.open, req.storyPoints, req.currentEstimatedTime, req.dueDate,
            req.description, req.subTickets?.map { s -> CreateTicket(s) }, req.existingSubTicketIds, req.partenTicket)
}