package io.ticktag.integrationtests.restinterface.ticket

import io.ticktag.ADMIN_ID
import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.restinterface.statistic.controller.StatisticController
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import javax.inject.Inject


class TicketProgressTest : ApiBaseTest() {
    @Inject
    lateinit var statisticController: StatisticController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testProgressTicketSamples.sql")
    }

    @Test
    fun `getTicketProgress should calculate correct Progress`() {
        withUser(ADMIN_ID) { ->
            val progress = statisticController.getTicketProgress(UUID.fromString("00000000-0003-0000-0000-000000000002"))

            assertEquals(20, progress.loggedTime.seconds)
            assertEquals(25, progress.currentEstimatedTime.seconds)
            assertEquals(50, progress.totalLoggedTime.seconds)
            assertEquals(60, progress.totalCurrentEstimatedTime.seconds)
        }
    }
}