package io.ticktag.restinterface.test

import io.ticktag.ADMIN_ID
import io.ticktag.USER_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.user.schema.CreateTicketRequestJson
import io.ticktag.restinterface.user.schema.UpdateTicketRequestJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.TicktagValidationException
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject

/**
 * Created by stefandraskovits on 18/11/2016.
 */
class TicketApiTest : ApiBaseTest() {
    @Inject
    lateinit var ticketController: TicketController

    @Test
    fun `createTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), emptyList(), null)

            val result = ticketController.createTicket(req, principal)
            assert(result.title.equals("test"))
            assert(result.open == true)
            assert(result.storyPoints == 4)
            assert(result.initialEstimatedTime?.equals(Duration.ofDays(1)) ?: false)
            assert(result.currentEstimatedTime?.equals(Duration.ofDays(1)) ?: false)
            assert(result.dueDate?.equals(now) ?: false)
            assert(result.description.equals("description"))
            assert(result.projectId.equals(UUID.fromString("00000000-0002-0000-0000-000000000001")))

        }
    }

    @Test
    fun `listTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000001"))
        }
    }


    @Test(expected = NotFoundException::class)
    fun `deleteTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            ticketController.deleteTicket(UUID.fromString("00000000-0003-0000-0000-000000000002"))
            ticketController.getTicket(UUID.fromString("00000000-0003-0000-0000-000000000002"))
        }
    }


    @Test
    fun `createTicketWithSubTasks positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), emptyList(), null)

            val req2 = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), null)

            val result = ticketController.createTicket(req2, principal)
            assert(result.title.equals("test"))
            assert(result.open == true)
            assert(result.storyPoints == 4)
            assert(result.initialEstimatedTime?.equals(Duration.ofDays(1)) ?: false)
            assert(result.currentEstimatedTime?.equals(Duration.ofDays(1)) ?: false)
            assert(result.dueDate?.equals(now) ?: false)
            assert(result.description.equals("description"))
            assert(result.projectId.equals(UUID.fromString("00000000-0002-0000-0000-000000000001")))
            assert(result.subTicketIds.size == 2)
            assert(result.subTicketIds.contains(UUID.fromString("00000000-0003-0000-0000-000000000001")))

        }
    }


    @Test(expected = TicktagValidationException::class)
    fun `createTicket negative because of Parent and SubTasks`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), emptyList(), null)

            val req2 = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), UUID.fromString("00000000-0003-0000-0000-000000000001"))

            ticketController.createTicket(req2, principal)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `createTicketWithInvalidSubTasks negative`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), listOf(UUID.fromString("00000000-0003-0000-0000-000000000003")), null)

            val req2 = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), null)

            ticketController.createTicket(req2, principal)
        }
    }


    @Test
    fun `updateTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = UpdateTicketRequestJson("test", true, 4, Duration.ofDays(1),
                    now, "description", emptyList(), emptyList(), emptyList(), null)
            val result = ticketController.updateTicket(req, UUID.fromString("00000000-0003-0000-0000-000000000001"), principal)
            assert(result.title.equals("test"))
            assert(result.open == true)
            assert(result.storyPoints == 4)
            assert(result.currentEstimatedTime?.equals(Duration.ofDays(1)) ?: false)
            assert(result.dueDate?.equals(now) ?: false)
            assert(result.description.equals("description"))

        }
    }


    @Test
    fun `updateTicket with SubTickets positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req2 = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), emptyList(), null)

            val req = UpdateTicketRequestJson("test", true, 4, Duration.ofDays(1),
                    now, "description", emptyList(), listOf(req2), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), null)
            val result = ticketController.updateTicket(req, UUID.fromString("00000000-0003-0000-0000-000000000002"), principal)
            assert(result.title.equals("test"))
            assert(result.open == true)
            assert(result.storyPoints == 4)
            assert(result.currentEstimatedTime?.equals(Duration.ofDays(1)) ?: false)
            assert(result.dueDate?.equals(now) ?: false)
            assert(result.description.equals("description"))
            assert(result.subTicketIds.size == 2)
            assert(result.subTicketIds.contains(UUID.fromString("00000000-0003-0000-0000-000000000001")))

        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `updateTicket with invalid SubTickets negativ`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req2 = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), emptyList(), null)
            val req = UpdateTicketRequestJson("test", true, 4, Duration.ofDays(1),
                    now, "description", emptyList(), listOf(req2), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), UUID.fromString("00000000-0003-0000-0000-000000000002"))
            val result = ticketController.updateTicket(req, UUID.fromString("00000000-0003-0000-0000-000000000002"), principal)

        }
    }


    @Test(expected = AccessDeniedException::class)
    fun `createTicket Permission negativ`() {
        withUser(USER_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000004"), emptyList(), emptyList(), emptyList(), null)
            val result = ticketController.createTicket(req, principal)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `listTicket Permission negativ`() {
        withUser(USER_ID) { principal ->
            ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000004"))
        }
    }


    @Test(expected = AccessDeniedException::class)
    fun `deleteTicket Permission negativ`() {
        withoutUser {
            ticketController.deleteTicket(UUID.fromString("00000000-0003-0000-0000-000000000002"))
        }
    }

}