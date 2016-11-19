package io.ticktag.service.project.dto

import io.ticktag.restinterface.user.schema.CreateTicketRequestJson
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.validation.constraints.Size

data class CreateTicket(
        val title: String,
        val open:Boolean,
        val storyPoints:Int?,
        val initialEstimatedTime: Duration?,
        val currentEstimatedTime: Duration?,
        val dueDate: Instant?,
        val description: String,
        val projectID: UUID,
        val subTickets: List<CreateTicket>,
        val existingSubTicketIds: List<UUID>,
        val partenTicket: UUID?
){constructor(req:CreateTicketRequestJson) : this (
        req.title,req.open,req.storyPoints,req.initialEstimatedTime,
        req.currentEstimatedTime,req.dueDate,req.description,req.projectId,req.subTickets.map { s->CreateTicket(s) },req.existingSubTicketIds,req.partenTicket)


}