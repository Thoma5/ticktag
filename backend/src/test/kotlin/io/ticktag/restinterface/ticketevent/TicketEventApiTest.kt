
import io.ticktag.USER_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.ticketevent.controllers.TicketEventController
import io.ticktag.restinterface.user.schema.UpdateTicketRequestJson
import io.ticktag.service.Principal
import org.junit.Assert
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject

class TicketEventApiTest : ApiBaseTest() {
    @Inject lateinit var ticketEventController: TicketEventController
    @Inject lateinit var ticketController: TicketController

    val ticketId = UUID.fromString("00000000-0003-0000-0000-000000000006")

    fun updateTicket(principal: Principal) {
        ticketController.updateTicket(UpdateTicketRequestJson("New Title", false, 20, Duration.ofMinutes(50), Instant.parse("2016-11-16T16:00:00Z"), "Added Models to Layout", null, null, null), ticketId, principal)
    }

    fun keepTicketSame(principal: Principal) {
        ticketController.updateTicket(UpdateTicketRequestJson("Added Models to Layout", true, 10, Duration.ofMinutes(25), Instant.parse("2016-12-12T23:38:23.085Z"), "New Description", null, null, null), ticketId, principal)
    }

    @Test
    fun test_eventTitleChangedShouldAddEvent(){
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            updateTicket(principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }

    @Test
    fun test_eventTitleChangedShouldNotAddEvent(){
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            keepTicketSame(principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore)
        }
    }


    @Test
    fun test_listTicketEvents_positive() {
        withUser(USER_ID) { principal ->
            val sizeBefore = ticketEventController.listTicketEvents(ticketId).size
            updateTicket(principal)
            Assert.assertEquals(ticketEventController.listTicketEvents(ticketId).size, sizeBefore + 1)
        }
    }
}
