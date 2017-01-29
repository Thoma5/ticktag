package io.ticktag.service.tickettag.services

import io.ticktag.service.tickettag.dto.CreateTicketTag
import io.ticktag.service.tickettag.dto.TicketTagResult
import io.ticktag.service.tickettag.dto.UpdateTicketTag
import java.util.*


interface TicketTagService {
    /**
     * Get a ticket tag, which can be used to tag a ticket
     */
    fun getTicketTag(id: UUID): TicketTagResult

    /**
     * get all Ticket Tags for a project
     */
    fun listTicketTagsInProject(projectId: UUID): List<TicketTagResult>

    /**
     * get all Ticket tags of a Ticket Tag Group
     */
    fun listTicketTagsInGroup(ticketTagGroupId: UUID): List<TicketTagResult>

    /**
     * Adds a Ticket Tag to a ticket tag group. This ticket will be added in the project of the ticket tag group
     */
    fun createTicketTag(ticketTag: CreateTicketTag, ticketTagGroupId: UUID): TicketTagResult

    /**
     * delete a Ticket Tag
     */
    fun deleteTicketTag(id: UUID)

    /**
     * Update a Ticket tag with the properties encapsulated in an Object
     */
    fun updateTicketTag(id: UUID, ticketTag: UpdateTicketTag): TicketTagResult
}