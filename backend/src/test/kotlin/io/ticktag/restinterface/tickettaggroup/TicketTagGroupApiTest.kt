package io.ticktag.restinterface.tickettaggroup

import io.ticktag.USER_ID
import io.ticktag.persistence.tickettaggroup.TicketTagGroupRepository
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.tickettaggroup.controllers.TicketTagGroupController
import io.ticktag.restinterface.tickettaggroup.schema.CreateTicketTagGroupRequestJson
import io.ticktag.restinterface.tickettaggroup.schema.UpdateTicketTagGroupRequestJson
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.inject.Inject


class TicketTagGroupApiTest : ApiBaseTest() {
    @Inject lateinit var ticketTagGroupController: TicketTagGroupController
    @Inject lateinit var ticketTagGroupRepo: TicketTagGroupRepository

    @Test
    fun getTicketTagGroup_positive() {
        withUser(USER_ID) { ->
            val tag = ticketTagGroupRepo.findAll().first()
            val resp = ticketTagGroupController.getTicketTagGroup(tag.id)
            assertEquals(tag.id, resp.id)
            assertEquals(tag.name, resp.name)
            assertEquals(tag.exclusive, resp.exclusive)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun getTicketTagGroup_negative() {
        withoutUser { ->
            val tag = ticketTagGroupRepo.findAll().first()
            ticketTagGroupController.getTicketTagGroup(tag.id)
        }
    }


    val ticketTagGroupToInsert = CreateTicketTagGroupRequestJson("ticket", true, UUID.fromString("00000000-0002-0000-0000-000000000001"))

    @Test
    fun createTicketTagGroup_positive() {
        withUser(USER_ID) { ->
            val resp = ticketTagGroupController.createTicketTagGroup(ticketTagGroupToInsert)
            val tag = ticketTagGroupRepo.findOne(resp.id)!!
            assertEquals(tag.id, resp.id)
            assertEquals(tag.name, resp.name)
            assertEquals(tag.exclusive, resp.exclusive)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun createTicketTagGroup_negative() {
        withoutUser { ->
            ticketTagGroupController.createTicketTagGroup(ticketTagGroupToInsert)
        }
    }


    val ticketTagGroupToUpdate = UpdateTicketTagGroupRequestJson("ticket", true, null)
    val ticketTagGroupToUpdateId = UUID.fromString("00000000-0009-0000-0000-000000000001")!!

    @Test
    fun updateTicketTagGroup_positive() {
        withUser(USER_ID) { ->
            val resp = ticketTagGroupController.updateTicketTagGroup(ticketTagGroupToUpdateId, ticketTagGroupToUpdate)
            val tag = ticketTagGroupRepo.findOne(resp.id)!!
            assertEquals(tag.id, resp.id)
            assertEquals(tag.name, resp.name)
            assertEquals(tag.exclusive, resp.exclusive)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun updateTicketTagGroup_negative() {
        withoutUser { ->
            ticketTagGroupController.updateTicketTagGroup(ticketTagGroupToUpdateId, ticketTagGroupToUpdate)
        }
    }

    val ticketTagGroupToDeleteId = UUID.fromString("00000000-0009-0000-0000-000000000001")!!

    @Test
    fun deleteTicketTagGroup_positive() {
        withUser(USER_ID) { ->
            ticketTagGroupController.deleteTicketTagGroup(ticketTagGroupToDeleteId)
            val tag = ticketTagGroupRepo.findOne(ticketTagGroupToDeleteId)
            if (tag != null) Assert.fail()
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun deleteTicketTagGroup_negative() {
        withoutUser { ->
            ticketTagGroupController.deleteTicketTagGroup(ticketTagGroupToDeleteId)
        }
    }

}