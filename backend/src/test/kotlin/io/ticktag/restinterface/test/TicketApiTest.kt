package io.ticktag.restinterface.test

import io.ticktag.ADMIN_ID
import io.ticktag.OBSERVER_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.user.schema.CreateTicketRequestJson
import io.ticktag.service.ticket.dto.TicketResult
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.inject.Inject

/**
 * Created by stefandraskovits on 18/11/2016.
 */
class TicketApiTest : ApiBaseTest(){
    @Inject
    lateinit var ticketController : TicketController

    @Test
    fun `createTicket positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now,"description",UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(),null)

            val result = ticketController.createTicket(req,principal)
            assert(result.title.equals("test"))
            assert(result.open == true)
            assert(result.storyPoints == 4)
            assert(result.initialEstimatedTime?.equals(Duration.ofDays(1))?:false)
            assert(result.currentEstimatedTime?.equals(Duration.ofDays(1))?:false)
            assert(result.dueDate?.equals(now)?:false)
            assert(result.description.equals("description"))
            assert(result.projectId.equals(UUID.fromString("00000000-0002-0000-0000-000000000001")))

        }
    }



    @Test
    fun `createTicketWithSubTasks positiv`() {
        withUser(ADMIN_ID) { principal ->
            val now = Instant.now()
            val req = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now,"description",UUID.fromString("00000000-0002-0000-0000-000000000001"), emptyList(), emptyList(),null)

            val req2 = CreateTicketRequestJson("test", true, 4, Duration.ofDays(1), Duration.ofDays(1),
                    now,"description",UUID.fromString("00000000-0002-0000-0000-000000000001"), listOf(req), listOf(UUID.fromString("00000000-0003-0000-0000-000000000001")),null)

            val result = ticketController.createTicket(req,principal)
            assert(result.title.equals("test"))
            assert(result.open == true)
            assert(result.storyPoints == 4)
            assert(result.initialEstimatedTime?.equals(Duration.ofDays(1))?:false)
            assert(result.currentEstimatedTime?.equals(Duration.ofDays(1))?:false)
            assert(result.dueDate?.equals(now)?:false)
            assert(result.description.equals("description"))
            assert(result.projectId.equals(UUID.fromString("00000000-0002-0000-0000-000000000001")))
            assert(result.subTicketIds.size==2)
            assert(result.subTicketIds.contains(UUID.fromString("00000000-0003-0000-0000-000000000001")))

        }
    }
}