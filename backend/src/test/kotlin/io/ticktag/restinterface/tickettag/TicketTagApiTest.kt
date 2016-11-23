package io.ticktag.restinterface.tickettag

import io.ticktag.USER_ID
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.tickettag.TicketTagRepository
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.tickettag.controllers.TicketTagController
import io.ticktag.restinterface.tickettag.schema.CreateTicketTagRequestJson
import io.ticktag.restinterface.tickettag.schema.UpdateTicketTagRequestJson
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.inject.Inject

class TicketTagApiTest : ApiBaseTest() {
    @Inject lateinit var ticketTagController: TicketTagController
    @Inject lateinit var ticketTagRepo: TicketTagRepository
    @Inject lateinit var ticketTagGroupRepo: TicketTagGroupRepository
    @Inject lateinit var projectRepo: ProjectRepository

    @Test
    fun getTicketTag_positive() {
        withUser(USER_ID) { ->
            val tag = ticketTagRepo.findAll().first()
            val resp = ticketTagController.getTicketTag(tag.id)
            assertEquals(tag.id, resp.id)
            assertEquals(tag.name, resp.name)
            assertEquals(tag.color, resp.color)
            assertEquals(tag.order, resp.order)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun getTicketTag_negative() {
        withoutUser { ->
            val tag = ticketTagRepo.findAll().first()
            ticketTagController.getTicketTag(tag.id)
        }
    }


    val ticketTagToInsert = CreateTicketTagRequestJson("ticket", "000000", 0, UUID.fromString("00000000-0009-0000-0000-000000000001"))

    @Test
    fun createTicketTag_positive() {
        withUser(USER_ID) { ->
            val resp = ticketTagController.createTicketTag(ticketTagToInsert)
            val tag = ticketTagRepo.findOne(resp.id)!!
            assertEquals(tag.id, resp.id)
            assertEquals(tag.name, resp.name)
            assertEquals(tag.color, resp.color)
            assertEquals(tag.order, resp.order)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun createTicketTag_negative() {
        withoutUser { ->
            ticketTagController.createTicketTag(ticketTagToInsert)
        }
    }


    val ticketTagToUpdate = UpdateTicketTagRequestJson("ticket", "000000", 0)
    val ticketTagToUpdateId = UUID.fromString("00000000-0005-0000-0000-000000000001")!!

    @Test
    fun updateTicketTag_positive() {
        withUser(USER_ID) { ->
            val resp = ticketTagController.updateTicketTag(ticketTagToUpdateId, ticketTagToUpdate)
            val tag = ticketTagRepo.findOne(resp.id)!!
            assertEquals(tag.id, resp.id)
            assertEquals(tag.name, resp.name)
            assertEquals(tag.color, resp.color)
            assertEquals(tag.order, resp.order)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun updateTicketTag_negative() {
        withoutUser { ->
            ticketTagController.updateTicketTag(ticketTagToUpdateId, ticketTagToUpdate)
        }
    }

    val ticketTagToDeleteId = UUID.fromString("00000000-0005-0000-0001-000000000003")!!

    @Test
    fun deleteTicketTag_positive() {
        withUser(USER_ID) { ->
            ticketTagController.deleteTicketTag(ticketTagToDeleteId)
            val tag = ticketTagRepo.findOne(ticketTagToDeleteId)
            if (tag != null) Assert.fail()
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun deleteTicketTag_negative() {
        withoutUser { ->
            ticketTagController.deleteTicketTag(ticketTagToDeleteId)
        }
    }

    @Test
    fun `listTicketTags should return BadRequest with two parameters`() {
        withUser(USER_ID) { ->
            val result = ticketTagController.listTicketTags(ticketTagGroupId = UUID.randomUUID(), projectId = UUID.randomUUID())
            assertEquals(result.statusCode, HttpStatus.BAD_REQUEST)
        }
    }

    @Test
    fun `listTicketTags should return BadRequest with no parameters`() {
        withUser(USER_ID) { ->
            val result = ticketTagController.listTicketTags(ticketTagGroupId = null, projectId = null)
            assertEquals(result.statusCode, HttpStatus.BAD_REQUEST)
        }
    }

    @Test
    fun `listTicketTags should pass with ticketTagGroupId parameter`() {
        withUser(USER_ID) { ->
            val group = ticketTagGroupRepo.findAll().first()
            val result = ticketTagController.listTicketTags(ticketTagGroupId = group.id, projectId = null)
            assertEquals(result.statusCode, HttpStatus.OK)
        }
    }

    @Test
    fun `listTicketTags should pass with projectId parameter`() {
        withUser(USER_ID) { ->
            val project = projectRepo.findAll().first()
            val result = ticketTagController.listTicketTags(ticketTagGroupId = null, projectId = project.id)
            assertEquals(result.statusCode, HttpStatus.OK)
        }
    }
}