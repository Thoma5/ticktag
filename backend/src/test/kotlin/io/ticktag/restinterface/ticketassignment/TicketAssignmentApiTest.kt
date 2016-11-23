package io.ticktag.restinterface.timecategory


import io.ticktag.*
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.ticketassignment.controllers.TicketAssignmentController
import io.ticktag.service.NotFoundException
import io.ticktag.service.TicktagValidationException
import org.junit.Assert.fail
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.inject.Inject

class TicketAssignmentApiTest : ApiBaseTest() {
    @Inject lateinit var ticketAssignmentController: TicketAssignmentController

    @Test
    fun `create and get ticketAssignment with User as ProjectUser should succeed`() {
        withUser(USER_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
        }
    }

    @Test
    fun `create and get ticketAssignment with Observer as ProjectAdmin should succeed`() {
        withUser(OBSERVER_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create and get ticketAssignment with Observer as ProjectObserver should fail`() {
        withUser(OBSERVER_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), USER_USER_ID)
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), USER_USER_ID)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create and get ticketAssignment with User as ProjectObserver should fail`() {
        withUser(USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), USER_USER_ID)
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), USER_USER_ID)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create ticketAssignment with wrong Data should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), UUID.fromString("00000000-0000-0000-0000-000000000000"))
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create ticketAssignment with others projects assignmentTag should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create ticketAssignment with user who isnt in project should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_NO_MEMBERS_ID, ASSTAG_PROJECT_NO_MEMBERS_IDS.first(), USER_USER_ID)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `assign ticket to ProjectObserver should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), USER_USER_ID)
        }
    }

    @Test(expected = TicktagValidationException::class)
    fun `assign ticket to Admin as ProjectObserver should fail`() {
        withUser(ADMIN_USER_ID) { principal ->
            ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), ADMIN_USER_ID)
        }
    }

    @Test
    fun `get ticketAssignment with Observer as ProjectObserver should succeed`() {
        withUser(OBSERVER_USER_ID) { principal ->
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_UOA_2_ID, ASSTAG_PROJECT_AOU_UOA_IDS.first(), ADMIN_USER_ID)
        }
    }

    @Test
    fun `get ticketAssignment with Admin as ProjectAdmin should succeed`() {
        withUser(ADMIN_USER_ID) { principal ->
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_USER_ID)
        }
    }

    @Test
    fun `get ticketAssignment with User as ProjectObserver should succeed`() {
        withUser(USER_USER_ID) { principal ->
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS.first(), ADMIN_USER_ID)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete ticketAssignment with User as ProjectObserver should fail`() {
        withUser(USER_USER_ID) { principal ->
            ticketAssignmentController.deleteTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_USER_ID)

        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete and ticketAssignment with Observer as ProjectUser should get`() {
        withUser(OBSERVER_USER_ID) { principal ->
            try {
                ticketAssignmentController.deleteTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_USER_ID)
            } catch (e: Exception) {
                fail()
            }
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_AUO_2_ID, ASSTAG_PROJECT_AOU_AUO_IDS[1], USER_USER_ID)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create get delete and get ticketAssignment with User as ProjectUser should fail`() {
        withUser(USER_USER_ID) { principal ->
            try {
                ticketAssignmentController.createTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
                ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
                ticketAssignmentController.deleteTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
            } catch (e: Exception) {
                fail()
            }
            ticketAssignmentController.getTicketAssignment(TICKET_PROJECT_AOU_OAU_0_ID, ASSTAG_PROJECT_AOU_OAU_IDS.first(), USER_USER_ID)
        }
    }


}

