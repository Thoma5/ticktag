package io.ticktag.service.tickettaggroup.service

import io.ticktag.service.tickettaggroup.dto.CreateTicketTagGroup
import io.ticktag.service.tickettaggroup.dto.TicketTagGroupResult
import io.ticktag.service.tickettaggroup.dto.UpdateTicketTagGroup
import java.util.*

interface TicketTagGroupService {
    fun getTicketTagGroup(id: UUID): TicketTagGroupResult?
    fun listTicketTagGroups(projectId: UUID): List<TicketTagGroupResult>
    fun createTicketTagGroup(ticketTagGroup: CreateTicketTagGroup): TicketTagGroupResult
    fun deleteTicketTagGroup(id: UUID)
    fun updateTicketTagGroup(id: UUID, ticketTagGroup: UpdateTicketTagGroup): TicketTagGroupResult
}