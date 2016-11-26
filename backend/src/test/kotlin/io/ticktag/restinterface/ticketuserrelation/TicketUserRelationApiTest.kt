package io.ticktag.restinterface.ticketuserrelation


import io.ticktag.*
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.ticketuserrelation.controllers.TicketUserRelationController
import io.ticktag.service.NotFoundException
import io.ticktag.service.TicktagValidationException
import org.junit.Assert.fail
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.inject.Inject

class TicketUserRelationApiTest : ApiBaseTest() {
    @Inject lateinit var ticketUserRelationController: TicketUserRelationController


    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }

    @Test
    fun `create and get ticketAssignment with User as ProjectUser should succeed`() {
        withUser(USER_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
        }
    }

    @Test
    fun `create and get ticketAssignment with Observer as ProjectAdmin should succeed`() {
        withUser(OBSERVER_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create and get ticketAssignment with Observer as ProjectObserver should fail`() {
        withUser(OBSERVER_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), USER_ID)
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), USER_ID)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create and get ticketAssignment with User as ProjectObserver should fail`() {
        withUser(USER_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), USER_ID)
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), USER_ID)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create ticketAssignment with wrong Data should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), UUID.fromString("00000000-0000-0000-0000-000000000000"))
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create ticketAssignment with others projects assignmentTag should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create ticketAssignment with user who isnt in project should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_NO_MEMBERS_ID, ASSTAG_PROJECT_NO_MEMBERS_IDS.first(), USER_ID)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `assign ticket to ProjectObserver should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), USER_ID)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `assign ticket to Admin as ProjectObserver should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), ADMIN_ID)
        }
    }

    @Test
    fun `get ticketAssignment with Observer as ProjectObserver should succeed`() {
        withUser(OBSERVER_ID) { principal ->
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), ADMIN_ID)
        }
    }

    @Test
    fun `get ticketAssignment with Admin as ProjectAdmin should succeed`() {
        withUser(ADMIN_ID) { principal ->
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_ID)
        }
    }

    @Test
    fun `get ticketAssignment with User as ProjectObserver should succeed`() {
        withUser(USER_ID) { principal ->
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), ADMIN_ID)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete ticketAssignment with User as ProjectObserver should fail`() {
        withUser(USER_ID) { principal ->
            ticketUserRelationController.deleteTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_ID)

        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete and ticketAssignment with Observer as ProjectUser should get`() {
        withUser(OBSERVER_ID) { principal ->
            try {
                ticketUserRelationController.deleteTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_ID)
            } catch (e: Exception) {
                fail()
            }
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_ID)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create get delete and get ticketAssignment with User as ProjectUser should fail`() {
        withUser(USER_ID) { principal ->
            try {
                ticketUserRelationController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
                ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
                ticketUserRelationController.deleteTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
            } catch (e: Exception) {
                fail()
            }
            ticketUserRelationController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_ID)
        }
    }

}

