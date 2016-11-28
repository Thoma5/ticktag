package io.ticktag.restinterface.ticketevent

import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.comment.controllers.CommentController
import io.ticktag.restinterface.comment.schema.UpdateCommentRequestJson
import io.ticktag.restinterface.loggedtime.controller.LoggedTimeController
import io.ticktag.restinterface.loggedtime.schema.CreateLoggedTimeJson
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.ticket.schema.UpdateTicketRequestJson
import io.ticktag.restinterface.ticketevent.controllers.TicketEventController
import io.ticktag.restinterface.ticketuserrelation.controllers.TicketUserRelationController
import org.junit.Assert
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject

class TicketEventApiTest : ApiBaseTest() {
    override fun loadTestData(): List<String> {
        return arrayListOf("samples.sql")
    }

    @Inject lateinit var ticketEventController: TicketEventController
    @Inject lateinit var ticketController: TicketController
    @Inject lateinit var commentController: CommentController
    @Inject lateinit var loggedTimeController: LoggedTimeController
    @Inject lateinit var ticketAssignmentController: TicketUserRelationController

    val ticketId = UUID.fromString("00000000-0003-0000-0000-000000000006")
    val parentTicketId = UUID.fromString("00000000-0003-0000-0000-000000000001")
    val commendId = UUID.fromString("00000000-0004-0000-0000-000000000008")
    val assignmentTagId = UUID.fromString("00000000-0006-0000-0000-000000000003")
    val loggedTimeId = UUID.fromString("00000000-0008-0000-0000-000000000001")
    val user = UUID.fromString("00000000-0001-0000-0000-000000000001")

  @Test
    fun test_ticketDescriptionChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, "New Description", null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketCurrentEstimatedTimeChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, Duration.ofHours(10), ticket.dueDate, ticket.description, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketDueDateChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, Instant.parse("2017-01-01T16:00:00Z"), ticket.description, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }


    @Test
    fun test_ticketLoggedTimeAddedShouldAddEvent() {
        val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000001")

        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            loggedTimeController.createLoggedTime(CreateLoggedTimeJson(Duration.ofHours(1), commendId, categoryId))

            val ticket = ticketController.getTicket(ticketId)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketLoggedTimeRemovedShouldAddEvent() {
        val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000001")

        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            loggedTimeController.deleteLoggedTime(loggedTimeId)

            val ticket = ticketController.getTicket(ticketId)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    //TODO mention added

    //TODO mention removed


    fun test_parentChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, Instant.parse("2017-01-01T16:00:00Z"), ticket.description, parentTicketId), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketEventTitleChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson("New Title", ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, ticket.description, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketStateChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, !ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, ticket.description, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketStoryPointsChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, 100, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, ticket.description, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    //TODO Tags added

    //TODO Tags removed

    @Test
    fun test_ticketUserAddedChangedShouldAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            ticketAssignmentController.createTicketAssignment(ticketId, assignmentTagId, principal.id, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketUserRemovedChangedShouldAddEvent() {
        withUser(user) { principal ->
            ticketAssignmentController.createTicketAssignment(ticketId, assignmentTagId, principal.id, principal)
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            ticketAssignmentController.deleteTicketAssignment(ticketId, assignmentTagId, principal.id, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    /** Negative Test for all Events **/

    @Test
    fun test_keepTicketSameShouldNotAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, ticket.description, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore)
        }
    }

    @Test
    fun test_ticketEventCommentChangedShouldNotAddEvent() {
        withUser(user) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val comment = commentController.getComment(commendId)
            commentController.updateComment(UpdateCommentRequestJson(comment.text, comment.mentionedTicketId, null), commendId)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore)
        }
    }
}
