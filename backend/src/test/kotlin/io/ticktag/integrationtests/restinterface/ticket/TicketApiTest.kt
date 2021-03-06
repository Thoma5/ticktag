package io.ticktag.integrationtests.restinterface.ticket

import io.ticktag.ADMIN_ID
import io.ticktag.PROJECT_AOU_AUO_ID
import io.ticktag.PROJECT_NO_MEMBERS_ID
import io.ticktag.USER_ID
import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.restinterface.UpdateNotnullValueJson
import io.ticktag.restinterface.UpdateNullableValueJson
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.ticket.schema.CreateTicketRequestJson
import io.ticktag.restinterface.ticket.schema.TicketSort
import io.ticktag.restinterface.ticket.schema.UpdateTicketRequestJson
import io.ticktag.restinterface.ticketuserrelation.schema.CreateTicketUserRelationRequestJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.TicktagValidationException
import io.ticktag.service.ValidationErrorDetail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.*
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject
import kotlin.test.assertFailsWith

class TicketApiTest : ApiBaseTest() {
    @Inject
    lateinit var ticketController: TicketController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }

    @Test
    fun `createTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), emptyList(), emptyList(), emptyList(), null, emptyList())

            val result = ticketController.createTicket(req, principal).body!!
            assertEquals(result.title, ("test"))
            assertEquals(result.number, 3)
            assertEquals(result.open, true)
            assertEquals(result.storyPoints, 4)
            assertEquals(result.initialEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.currentEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.dueDate, now)
            assertEquals(result.description, "description")
            assertEquals(result.projectId, UUID.fromString("00000000-0002-0000-0000-000000000101"))

        }
    }

    @Test
    fun `createTicketWithAssignments positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val assignments = ArrayList<CreateTicketUserRelationRequestJson>()
            assignments.add(CreateTicketUserRelationRequestJson(UUID.fromString("00000000-0006-0000-0000-000000000102"), UUID.fromString("00000000-0001-0000-0000-000000000101")))
            assignments.add(CreateTicketUserRelationRequestJson(UUID.fromString("00000000-0006-0000-0000-000000000103"), UUID.fromString("00000000-0001-0000-0000-000000000102")))
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), assignments, emptyList(), emptyList(), null, emptyList())

            val result = ticketController.createTicket(req, principal).body!!
            assertThat(result.title, `is`("ticket"))
            assertEquals(result.open, true)
            assertThat(result.storyPoints, `is`(4))
            assertThat(result.initialEstimatedTime, `is`(Duration.ofDays(1)))
            assertThat(result.currentEstimatedTime, `is`(Duration.ofDays(1)))
            assertThat(result.dueDate, `is`(now))
            assertThat(result.description, `is`("description"))
            assertThat(result.projectId, `is`(UUID.fromString("00000000-0002-0000-0000-000000000101")))
            assertThat(result.ticketUserRelations.size, `is`(2))

        }
    }

    @Test
    fun `listTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            val list = ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000101"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 2, listOf(TicketSort.TITLE_ASC))
            assertEquals(list.size, 2)
        }
    }


    @Test(expected = NotFoundException::class)
    fun `deleteTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            ticketController.deleteTicket(UUID.fromString("00000000-0003-0000-0000-000000000102"))
            ticketController.getTicket(UUID.fromString("00000000-0003-0000-0000-000000000102"))
        }
    }


    @Test
    fun `createTicketWithSubTasks positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), emptyList(), emptyList(), emptyList(), null, emptyList())

            val req2 = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000101")), null, emptyList())

            val result = ticketController.createTicket(req2, principal).body!!
            assertEquals(result.title, ("ticket"))
            assertEquals(result.open, true)
            assertEquals(result.storyPoints, 4)
            assertEquals(result.initialEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.currentEstimatedTime, (Duration.ofDays(1)))
            assertEquals(result.dueDate, now)
            assertEquals(result.description, "description")
            assertEquals(result.projectId, UUID.fromString("00000000-0002-0000-0000-000000000101"))
            assertEquals(result.subTicketIds.size, 2)
            assertThat(result.subTicketIds, `hasItem`(UUID.fromString("00000000-0003-0000-0000-000000000101")))

        }
    }

    @Test
    fun `create of ticket with nested subtickets should fail`() {
        withUser(ADMIN_ID) { principal ->
            val reqSubSub = CreateTicketRequestJson("subsub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val reqSub = CreateTicketRequestJson("sub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), listOf(reqSubSub), emptyList(), null, emptyList())
            val reqParent = CreateTicketRequestJson("parent", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), listOf(reqSub), emptyList(), null, emptyList())

            val ex = assertFailsWith(TicktagValidationException::class, { ticketController.createTicket(reqParent, principal) })
            assertThat(ex.errors.size, `is`(1))
            val err = ex.errors[0]
            assertThat(err.field, `is`("createTicket"))
            val detail = err.detail
            if (detail is ValidationErrorDetail.Other) {
                assertThat(detail.name, `is`("nonestedsubtickets"))
            } else {
                fail("Wrong error type: " + err.detail.javaClass.kotlin)
            }
        }
    }

    @Test
    fun `create that would create a nested subticket should fail`() {
        withUser(ADMIN_ID) { principal ->
            val reqSub = CreateTicketRequestJson("sub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val reqParent = CreateTicketRequestJson("parent", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), listOf(reqSub), emptyList(), null, emptyList())

            val body = ticketController.createTicket(reqParent, principal).body!!
            val reqSubSub = CreateTicketRequestJson("subsub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), emptyList(), emptyList(), body.subTicketIds[0], emptyList())

            val ex = assertFailsWith(TicktagValidationException::class, { ticketController.createTicket(reqSubSub, principal) })

            assertThat(ex.errors.size, `is`(1))
            val err = ex.errors[0]
            assertThat(err.field, `is`("createTicket"))
            val detail = err.detail
            if (detail is ValidationErrorDetail.Other) {
                assertThat(detail.name, `is`("nonestedsubtickets"))
            } else {
                fail("Wrong error type: " + err.detail.javaClass.kotlin)
            }
        }
    }

    @Test
    fun `update that would turn the ticket into a nested subticket should fail`() {
        withUser(ADMIN_ID) { principal ->
            val reqSub = CreateTicketRequestJson("sub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val reqParent = CreateTicketRequestJson("parent", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), listOf(reqSub), emptyList(), null, emptyList())
            val parentBody = ticketController.createTicket(reqParent, principal).body!!
            val subId = parentBody.subTicketIds[0]
            val reqSubSub = CreateTicketRequestJson("subsub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val subSubBody = ticketController.createTicket(reqSubSub, principal).body!!

            val updateSubSub = UpdateTicketRequestJson(null, null, null, null, null, null, null, UpdateNullableValueJson(subId), null)
            val ex = assertFailsWith(TicktagValidationException::class, { ticketController.updateTicket(updateSubSub, subSubBody.id, principal) })

            assertThat(ex.errors.size, `is`(1))
            val err = ex.errors[0]
            assertThat(err.field, `is`("updateTicket"))
            val detail = err.detail
            if (detail is ValidationErrorDetail.Other) {
                assertThat(detail.name, `is`("nonestedsubtickets"))
            } else {
                fail("Wrong error type: " + err.detail.javaClass.kotlin)
            }
        }
    }

    @Test
    fun `update that would turn subtickets into nested subtickets should fail`() {
        withUser(ADMIN_ID) { principal ->
            val reqSubSub = CreateTicketRequestJson("subsub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val reqSub = CreateTicketRequestJson("sub", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), listOf(reqSubSub), emptyList(), null, emptyList())
            val reqParent = CreateTicketRequestJson("parent", true, null, null, null, null, "description",
                    PROJECT_NO_MEMBERS_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val parentBody = ticketController.createTicket(reqParent, principal).body!!
            val subBody = ticketController.createTicket(reqSub, principal).body!!

            val updateSub = UpdateTicketRequestJson(null, null, null, null, null, null, null, UpdateNullableValueJson(parentBody.id), null)
            val ex = assertFailsWith(TicktagValidationException::class, { ticketController.updateTicket(updateSub, subBody.id, principal) })

            assertThat(ex.errors.size, `is`(1))
            val err = ex.errors[0]
            assertThat(err.field, `is`("updateTicket"))
            val detail = err.detail
            if (detail is ValidationErrorDetail.Other) {
                assertThat(detail.name, `is`("nonestedsubtickets"))
            } else {
                fail("Wrong error type: " + err.detail.javaClass.kotlin)
            }
        }
    }

    @Test
    fun `update with initial not set but current set should update estimated (and current if null)`() {
        withUser(ADMIN_ID) { p ->
            val create = CreateTicketRequestJson("title", true, null, null, null, null, "description",
                    PROJECT_AOU_AUO_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val ticket = ticketController.createTicket(create, p).body!!

            val req = UpdateTicketRequestJson(null, null, null, null, UpdateNullableValueJson(Duration.ofDays(2)), null, null, null, null)
            val result = ticketController.updateTicket(req, ticket.id, p)

            assertEquals(req.currentEstimatedTime!!.value, result.initialEstimatedTime)
            assertEquals(req.currentEstimatedTime!!.value, result.initialEstimatedTime)
        }
    }

    @Test
    fun `update with current not set yet should set it to initial`() {
        withUser(ADMIN_ID) { p ->
            val create = CreateTicketRequestJson("title", true, null, null, null, null, "description",
                    PROJECT_AOU_AUO_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val ticket = ticketController.createTicket(create, p).body!!

            val req = UpdateTicketRequestJson(null, null, null, UpdateNullableValueJson(Duration.ofDays(2)), null, null, null, null, null)
            val result = ticketController.updateTicket(req, ticket.id, p)

            assertEquals(result.initialEstimatedTime, req.initialEstimatedTime!!.value)
            assertEquals(result.currentEstimatedTime, req.initialEstimatedTime!!.value)
        }
    }

    @Test
    fun `update with initial already set should not changed it to current`() {
        withUser(ADMIN_ID) { p ->
            val create = CreateTicketRequestJson("title", true, null, Duration.ofDays(1), null, null, "description",
                    PROJECT_AOU_AUO_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val ticket = ticketController.createTicket(create, p).body!!

            val req = UpdateTicketRequestJson(null, null, null, null, UpdateNullableValueJson(Duration.ofDays(2)), null, null, null, null)
            val result = ticketController.updateTicket(req, ticket.id, p)

            assertEquals(result.initialEstimatedTime, create.initialEstimatedTime)
            assertEquals(result.currentEstimatedTime, req.currentEstimatedTime!!.value)
        }
    }

    @Test
    fun `create with only initial should set current to same value`() {
        withUser(ADMIN_ID) { p ->
            val create = CreateTicketRequestJson("title", true, null, Duration.ofDays(1), null, null, "description",
                    PROJECT_AOU_AUO_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val result = ticketController.createTicket(create, p).body!!

            assertEquals(result.initialEstimatedTime, create.initialEstimatedTime)
            assertEquals(result.currentEstimatedTime, create.initialEstimatedTime)
        }
    }

    @Test
    fun `create with only current should set initial to same value`() {
        withUser(ADMIN_ID) { p ->
            val create = CreateTicketRequestJson("title", true, null, null, Duration.ofDays(1), null, "description",
                    PROJECT_AOU_AUO_ID, emptyList(), emptyList(), emptyList(), null, emptyList())
            val result = ticketController.createTicket(create, p).body!!

            assertEquals(result.initialEstimatedTime, create.currentEstimatedTime)
            assertEquals(result.currentEstimatedTime, create.currentEstimatedTime)
        }
    }


    @Test(expected = TicktagValidationException::class)
    fun `createTicket negative because of Parent and SubTasks`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), emptyList(), emptyList(), emptyList(), null, emptyList())

            val req2 = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000101")), UUID.fromString("00000000-0003-0000-0000-000000000101"), emptyList())

            ticketController.createTicket(req2, principal)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `createTicketWithInvalidSubTasks negative`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), emptyList(), emptyList(), listOf(UUID.fromString("00000000-0003-0000-0000-000000000003")), null, emptyList())

            val req2 = CreateTicketRequestJson("ticket", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000101"), emptyList(), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000101")), null, emptyList())

            ticketController.createTicket(req2, principal)
        }
    }


    @Test
    fun `updateTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = UpdateTicketRequestJson(
                    UpdateNotnullValueJson("ticket"),
                    UpdateNotnullValueJson(true),
                    UpdateNullableValueJson(4),
                    UpdateNullableValueJson(Duration.ofDays(1)),
                    UpdateNullableValueJson(Duration.ofDays(2)),
                    UpdateNullableValueJson(now),
                    UpdateNotnullValueJson("description"),
                    null,
                    null)
            val result = ticketController.updateTicket(req, UUID.fromString("00000000-0003-0000-0000-000000000101"), principal)
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
                    now, "description", UUID.fromString("00000000-0002-0000-0000-000000000004"), emptyList(), emptyList(), emptyList(), null, emptyList())
            ticketController.createTicket(req, principal)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `listTicket Permission negativ`() {
        withUser(USER_ID) { principal ->
            ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000004"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 2, listOf(TicketSort.STORY_POINTS_ASC))
        }
    }


    @Test(expected = AccessDeniedException::class)
    fun `deleteTicket Permission negativ`() {
        withoutUser {
            ticketController.deleteTicket(UUID.fromString("00000000-0003-0000-0000-000000000102"))
        }
    }

    @Test
    fun `listTicket test page number positiv`() {
        withUser(ADMIN_ID) { principal ->
            val list1 = ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000101"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 1, listOf(TicketSort.TITLE_ASC))
            val list2 = ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000101"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 1, 1, listOf(TicketSort.TITLE_ASC))

            assertFalse(list1.contains(list2.elementAt(0)))
            assertFalse(list1.contains(list2.elementAt(0)))
        }
    }


    @Test
    fun `listTicket test sorting Number positiv`() {
        withUser(ADMIN_ID) { principal ->
            val list1 = ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000101"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 50, listOf(TicketSort.NUMBER_ASC))
            assertEquals(2, list1.content.size)
            assertTrue(list1.content[0].number <= list1.content[1].number)
        }
    }

    @Test
    fun `listTicket test sorting dueDate positiv`() {
        withUser(ADMIN_ID) { principal ->
            val list1 = ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000101"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 50, listOf(TicketSort.DUE_DATE_ASC))
            assertEquals(2, list1.content.size)
            var i = 1
            while (i < list1.content.size) {
                val dueDate2 = list1.content[i].dueDate
                val dueDate1 = list1.content[i - 1].dueDate
                if (dueDate1 != null && dueDate2 != null) {
                    assertNotEquals(dueDate2.compareTo(dueDate1), -1)
                }
                i++
            }

        }
    }


    @Test
    fun `listTicket test sorting title positiv`() {
        withUser(ADMIN_ID) { principal ->
            val list1 = ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000101"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 50, listOf(TicketSort.TITLE_ASC))
            assertEquals(2, list1.content.size)
            var i = 1
            while (i < list1.content.size) {
                assertNotEquals(list1.content[i].title.compareTo(list1.content[i - 1].title), -1)
                i++
            }

        }
    }

    @Test
    fun `listTicket test sorting storypoints positiv`() {
        withUser(ADMIN_ID) { principal ->
            val list1 = ticketController.listTickets(UUID.fromString("00000000-0002-0000-0000-000000000101"), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, 50, listOf(TicketSort.STORY_POINTS_ASC))
            assertEquals(2, list1.content.size)
            assertNotEquals(null, list1.content[0].storyPoints)
            assertNotEquals(null, list1.content[1].storyPoints)
            assertTrue(list1.content[0].storyPoints ?: Integer.MAX_VALUE <= list1.content[1].storyPoints ?: Integer.MIN_VALUE)
        }
    }

    @Test
    fun `listTicketsFuzzy should find some tickets`() {
        withUser(ADMIN_ID) { ->
            val tickets = ticketController.listTicketsFuzzy(UUID.fromString("00000000-0002-0000-0000-000000000101"), "TiCK", listOf(TicketSort.NUMBER_ASC))

            assertEquals(2, tickets.size)
            assertEquals(listOf(1, 2), tickets.map { it.number })
        }
    }

    @Test(expected = org.springframework.security.access.AccessDeniedException::class)
    fun `listTicketsFuzzy should only work for project members`() {
        withUser(UUID.fromString("00000000-0001-0000-0000-000000000103")) { ->
            ticketController.listTicketsFuzzy(UUID.fromString("00000000-0002-0000-0000-000000000104"), "TiCK", listOf(TicketSort.NUMBER_ASC))
        }
    }
}