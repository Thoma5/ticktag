package io.ticktag.restinterface.ticket

import io.ticktag.ADMIN_ID
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.ticket.controllers.TicketController
import org.junit.Assert
import org.junit.Test
import java.util.*
import javax.inject.Inject


class TicketProgressTest : ApiBaseTest() {
    @Inject
    lateinit var ticketController: TicketController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testProgressTicketSamples.sql")
    }

    @Test
    fun `getTicketProgress should calculate correct Progress`() {
        withUser(ADMIN_ID) { ->
            val progress = ticketController.getTicketProgress(UUID.fromString("00000000-0003-0000-0000-000000000002"))

            Assert.assertEquals(progress.duration.seconds, 50)
            Assert.assertEquals(progress.currentEstimatedTime.seconds, 60)
        }
    }
}