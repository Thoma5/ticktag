package io.ticktag.service.tickettag.services

import io.ticktag.service.tickettag.dto.CreateTicketTag
import io.ticktag.service.tickettag.dto.TicketTagResult
import io.ticktag.service.tickettag.dto.UpdateTicketTag
import java.util.*


interface TicketTagService {
    fun getTicketTag(id: UUID): TicketTagResult
    fun listTicketTags(ticketTagGroupID: UUID): List<TicketTagResult>
    fun createTicketTag(ticketTag: CreateTicketTag, ticketTagGroupID: UUID): TicketTagResult
    fun deleteTicketTag(id: UUID)
    fun updateTicketTag(id: UUID, ticketTag: UpdateTicketTag) : TicketTagResult
}