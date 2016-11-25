package io.ticktag.restinterface.ticket

import io.ticktag.ADMIN_ID
import io.ticktag.USER_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.ticket.schema.CreateTicketRequestJson
import io.ticktag.restinterface.ticketuserrelation.schema.CreateTicketUserRelationRequestJson
import io.ticktag.restinterface.ticket.schema.TicketSort
import io.ticktag.restinterface.ticket.schema.UpdateTicketRequestJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.TicktagValidationException
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject

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
            assertEquals(result.title, ("test"))
            assertEquals(result.open, true)
            assertEquals(result.storyPoints, 4)
            assertEquals(result.initialEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.currentEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.dueDate, now)
            assertEquals(result.description, "description")
            assertEquals(result.projectId, UUID.fromString("00000000-0002-0000-0000-000000000001"))

        }
    }

    @Test
    fun `createTicketWithAssignments positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val assignments = ArrayList<CreateTicketUserRelationRequestJson>()
            assignments.add(CreateTicketUserRelationRequestJson(UUID.fromString("00000000-0006-0000-0000-000000000006"), UUID.fromString("00000000-0001-0000-0000-000000000001")))
            assignments.add(CreateTicketUserRelationRequestJson(UUID.fromString("00000000-0006-0000-0000-000000000005"), UUID.fromString("00000000-0001-0000-0000-000000000002")))
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), assignments, emptyList(), emptyList(), null)

            val result = ticketController.createTicket(req, principal)
            assertThat(result.title, `is`("ticket"))
            assertEquals(result.open, true)
            assertThat(result.storyPoints, `is`(4))
            assertThat(result.initialEstimatedTime, `is`(Duration.ofDays(1)))
            assertThat(result.currentEstimatedTime, `is`(Duration.ofDays(1)))
            assertThat(result.dueDate, `is`(now))
            assertThat(result.description, `is`("description"))
            assertThat(result.projectId, `is`(UUID.fromString("00000000-0002-0000-0000-000000000001")))
            assertThat(result.tickerUserRelations!!.size, `is`(2))

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
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), emptyList(), null)

            val req2 = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), null)

            val result = ticketController.createTicket(req2, principal)
            assertEquals(result.title, ("ticket"))
            assertEquals(result.open, true)
            assertEquals(result.storyPoints, 4)
            assertEquals(result.initialEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.currentEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.dueDate, now)
            assertEquals(result.description, "description")
            assertEquals(result.projectId, UUID.fromString("00000000-0002-0000-0000-000000000001"))
            assertEquals(result.subTicketIds.size, 2)
            assertThat(result.subTicketIds, `hasItem`(UUID.fromString("00000000-0003-0000-0000-000000000001")))

        }
    }


    @Test(expected = TicktagValidationException::class)
    fun `createTicket negative because of Parent and SubTasks`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), emptyList(), null)

            val req2 = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), UUID.fromString("00000000-0003-0000-0000-000000000001"))

            ticketController.createTicket(req2, principal)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `createTicketWithInvalidSubTasks negative`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(), listOf(UUID.fromString("00000000-0003-0000-0000-000000000003")), null)

            val req2 = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")), null)

            ticketController.createTicket(req2, principal)
        }
    }


    @Test
    fun `updateTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = UpdateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(2),
                    now, "description", null)
            val result = ticketController.updateTicket(req, UUID.fromString("00000000-0003-0000-0000-000000000001"), principal)
            assertEquals(result.title, "ticket")
            assertEquals(result.open, true)
            assertEquals(result.storyPoints, 4)
            assertEquals(result.initialEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.currentEstimatedTime, (Duration.ofDays(2)))
            assertEquals(result.dueDate, (now))
            assertEquals(result.description, ("description"))

        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `createTicket Permission negativ`() {
        withUser(USER_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
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

    @Test
    fun `listTicketsFuzzy should find some tickets`() {
        withUser(ADMIN_ID) { ->
            val tickets = ticketController.listTicketsFuzzy(UUID.fromString("00000000-0002-0000-0000-000000000001"), "USerS", listOf(TicketSort.NUMBER_ASC))

            assertEquals(4, tickets.size)
            assertEquals(tickets.map { it.number }, listOf(2, 3, 4, 5))
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun `listTicketsFuzzy should only work for project members`() {
        withUser(UUID.fromString("00000000-0001-0000-0000-000000000004")) { ->
            ticketController.listTicketsFuzzy(UUID.fromString("00000000-0002-0000-0000-000000000001"), "USerS", listOf(TicketSort.NUMBER_ASC))
        }
    }
}