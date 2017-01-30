package io.ticktag.service.tickettaggroup.service

import io.ticktag.service.tickettaggroup.dto.CreateTicketTagGroup
import io.ticktag.service.tickettaggroup.dto.TicketTagGroupResult
import io.ticktag.service.tickettaggroup.dto.UpdateTicketTagGroup
import java.util.*

interface TicketTagGroupService {
    /**
     * get a specific ticket tag group
     * @param id id of the ticket tag group
     */
    fun getTicketTagGroup(id: UUID): TicketTagGroupResult

    /**
     * list all Ticket tag groups of a project
     */
    fun listTicketTagGroups(projectId: UUID): List<TicketTagGroupResult>

    /**
     * create a Ticket tag group in a specific project
     */
    fun createTicketTagGroup(ticketTagGroup: CreateTicketTagGroup, projectId: UUID): TicketTagGroupResult

    /**
     * Delte a Ticket Tag group and all its Ticket tags
     */
    fun deleteTicketTagGroup(id: UUID)

    /**
     * Update a Ticket Tag Group.
     * The project can't be changed.
     */
    fun updateTicketTagGroup(id: UUID, ticketTagGroup: UpdateTicketTagGroup): TicketTagGroupResult
}