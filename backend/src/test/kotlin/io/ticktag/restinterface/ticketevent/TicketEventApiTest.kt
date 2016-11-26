import io.ticktag.USER_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.comment.controllers.CommentController
import io.ticktag.restinterface.comment.schema.UpdateCommentRequestJson
import io.ticktag.restinterface.loggedTime.controller.LoggedTimeController
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.ticket.schema.UpdateTicketRequestJson
import io.ticktag.restinterface.ticketevent.controllers.TicketEventController
import io.ticktag.service.Principal
import io.ticktag.service.comment.dto.CreateLoggedTimeJson
import org.junit.Assert
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject

class TicketEventApiTest : ApiBaseTest() {
    @Inject lateinit var ticketEventController: TicketEventController
    @Inject lateinit var ticketController: TicketController
    @Inject lateinit var commentController: CommentController
    @Inject lateinit var loggedTimeController: LoggedTimeController

    val ticketId = UUID.fromString("00000000-0003-0000-0000-000000000006")
    val commendId = UUID.fromString("00000000-0004-0000-0000-000000000008")


    fun updateTicket(principal: Principal) {
        ticketController.updateTicket(UpdateTicketRequestJson("New Title", false, 20, Duration.ofMinutes(50), Duration.ofMinutes(100), Instant.parse("2016-11-16T16:00:00Z"), "Added Models to Layout", null, null, null, null), ticketId, principal)
    }

    fun keepTicketSame(principal: Principal) {
        val ticket = ticketController.getTicket(ticketId)
        ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, true, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, ticket.description, null, null, null, null), ticketId, principal)
    }

    fun updateComment(principal: Principal) {
        //  commentController.updateComment(UpdateCommentRequestJson("Changed Comment"), , commendId, principal)
    }

    fun keepCommentSame(principal: Principal) {
        val comment = commentController.getComment(commendId)
        commentController.updateComment(UpdateCommentRequestJson(comment.text, comment.mentionedTicketId, null), commendId, principal)
    }


    @Test
    fun test_ticketEventCommentChangedShouldAddEvent() {
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val comment = commentController.getComment(commendId)
            commentController.updateComment(UpdateCommentRequestJson("New Comment Text", comment.mentionedTicketId, null), commendId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketCurrentEstimatedTimeChangedShouldAddEvent() {
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, Duration.ofHours(10), ticket.dueDate, ticket.description, null, null, null, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketLoggedTimeAddedShouldAddEvent() {
        val categoryId = UUID.fromString("00000000-0007-0000-0000-000000000001")

        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            loggedTimeController.createLoggedTime(CreateLoggedTimeJson(Duration.ofHours(1), commendId, categoryId))

            val ticket = ticketController.getTicket(ticketId)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketEventTitleChangedShouldAddEvent() {
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson("New Title", ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, ticket.description, null, null, null, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_ticketDescriptionChangedShouldAddEvent() {
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, "New Description", null, null, null, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    /** Negative Test for all Events **/

    @Test
    fun test_keepTicketSameShouldNotAddEvent() {
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val ticket = ticketController.getTicket(ticketId)
            ticketController.updateTicket(UpdateTicketRequestJson(ticket.title, ticket.open, ticket.storyPoints, ticket.initialEstimatedTime, ticket.currentEstimatedTime, ticket.dueDate, ticket.description, null, null, null, null), ticketId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore)
        }
    }

    @Test
    fun test_ticketEventCommentChangedShouldNotAddEvent() {
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            val comment = commentController.getComment(commendId)
            commentController.updateComment(UpdateCommentRequestJson(comment.text, comment.mentionedTicketId, null), commendId, principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore)
        }
    }

}
