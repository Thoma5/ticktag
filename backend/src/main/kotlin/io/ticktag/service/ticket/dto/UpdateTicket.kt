package io.ticktag.service.project.dto

import io.ticktag.restinterface.user.schema.CreateTicketRequestJson
import io.ticktag.restinterface.user.schema.UpdateTicketRequestJson
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.Size

data class UpdateTicket(
        val title: String?,
        val open:Boolean?,
        val storyPoints:Int?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String?,
        val subTickets: List<CreateTicket>?,
        val existingSubTicketIds: List<UUID>?,
        val partenTicket: UUID?

){constructor(req:UpdateTicketRequestJson) : this (
        req.title,req.open,req.storyPoints, req.currentEstimatedTime,req.dueDate,
        req.description,req.subTickets?.map { s->CreateTicket(s) },req.existingSubTicketIds,req.partenTicket)
}