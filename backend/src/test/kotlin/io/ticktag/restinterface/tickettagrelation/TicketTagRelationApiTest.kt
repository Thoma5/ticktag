package io.ticktag.restinterface.tickettagrelation


import io.ticktag.*
import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.tickettagrelation.controllers.TicketTagRelationController
import io.ticktag.service.NotFoundException
import junit.framework.Assert.fail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.springframework.security.access.AccessDeniedException
import java.util.*
import javax.inject.Inject
import kotlin.test.assertFailsWith

class TicketTagRelationApiTest : ApiBaseTest() {
    @Inject lateinit var ticketTagRelationController: TicketTagRelationController

    override fun loadTestData(): List<String> {
        return arrayListOf("sql/testBaseSamples.sql")
    }

    @Test
    fun `create and get ticketTagRelation with User as ProjectUser should succeed`() {
        withUser(USER_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
            val relation = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())
            assertThat(relation.tagId.toString(), `is`(TAG_AOU_OAU_G1.first().toString()))
            assertThat(relation.ticketId.toString(), `is`(TICKET_PROJECT_AOU_OAU_0_ID.toString()))
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create with tag from wrong project should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_OAU_G1.first(), principal)
        }
    }


    @Test
    fun `create relation with existing relation from exclusive group should replace it`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1[0], principal)
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1[1], principal)

            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1[1])
            assertFailsWith(NotFoundException::class, {
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1[0])
            })
        }
    }

    @Test
    fun `tag assignment duplication should not throw exceptions`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1[0], principal)
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1[0], principal)
        }
    }

    @Test
    fun `non exclusive Tag Group Constraint creation should succeed`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G3[0], principal)
            val relation1 = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G3[0])
            assertThat(relation1.tagId.toString(), `is`(TAG_AOU_AUO_G3[0].toString()))
            assertThat(relation1.ticketId.toString(), `is`(TICKET_PROJECT_AOU_AUO_2_ID.toString()))

            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G3[1], principal)
            val relation2 = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G3[1])
            assertThat(relation2.tagId.toString(), `is`(TAG_AOU_AUO_G3[1].toString()))
            assertThat(relation2.ticketId.toString(), `is`(TICKET_PROJECT_AOU_AUO_2_ID.toString()))
        }
    }


    @Test
    fun `create and get ticketTagRelation with User as ProjectAdmin should succeed`() {
        withUser(USER_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
            val relation = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first())
            assertThat(relation.tagId.toString(), `is`(TAG_AOU_UOA_G1.first().toString()))
            assertThat(relation.ticketId.toString(), `is`(TICKET_PROJECT_AOU_UOA_2_ID.toString()))
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create ticketTagRelation with User as ProjectObserver should fail`() {
        withUser(USER_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1.first(), principal)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `create ticketTagRelation with wrong id should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(UUID.fromString("00000000-0000-0000-0000-000000000000"), TAG_AOU_AUO_G1.first(), principal)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `create ticketTagRelation with Observer as ProjectObserver should fail`() {
        withUser(OBSERVER_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
        }
    }

    @Test
    fun `create and get ticketTagRelation with Observer as ProjectAdmin should succeed`() {
        withUser(OBSERVER_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
            val relation = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())
            assertThat(relation.tagId.toString(), `is`(TAG_AOU_OAU_G1.first().toString()))
            assertThat(relation.ticketId.toString(), `is`(TICKET_PROJECT_AOU_OAU_0_ID.toString()))
        }
    }

    @Test
    fun `create and get ticketTagRelation with Observer as ProjectUser should succeed`() {
        withUser(OBSERVER_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first(), principal)
            val relation = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first())
            assertThat(relation.tagId.toString(), `is`(TAG_AOU_AUO_G2.first().toString()))
            assertThat(relation.ticketId.toString(), `is`(TICKET_PROJECT_AOU_AUO_2_ID.toString()))
        }
    }

    @Test
    fun `create and get ticketTagRelation with Admin as ProjectObserver should succeed`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
            val relation = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())
            assertThat(relation.tagId.toString(), `is`(TAG_AOU_OAU_G1.first().toString()))
            assertThat(relation.ticketId.toString(), `is`(TICKET_PROJECT_AOU_OAU_0_ID.toString()))
        }
    }

    @Test
    fun `create and get ticketTagRelation with Admin as ProjectAdmin should succeed`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first(), principal)
            val relation = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first())
            assertThat(relation.tagId.toString(), `is`(TAG_AOU_AUO_G2.first().toString()))
            assertThat(relation.ticketId.toString(), `is`(TICKET_PROJECT_AOU_AUO_2_ID.toString()))
        }
    }

    @Test
    fun `create and get ticketTagRelation with Admin as ProjectUser should succeed`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
            val relation = ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first())
            assertThat(relation.tagId.toString(), `is`(TAG_AOU_UOA_G1.first().toString()))
            assertThat(relation.ticketId.toString(), `is`(TICKET_PROJECT_AOU_UOA_2_ID.toString()))
        }
    }

    //Delete
    @Test(expected = NotFoundException::class)
    fun `delete ticketTagRelation with User as ProjectUser should succeed`() {
        withUser(USER_ID) { principal ->
            try {
                ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())
                ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
            } catch (e: Exception) {
                fail()
            }
            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())

        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete ticketTagRelation with User as ProjectAdmin should succeed`() {
        withUser(USER_ID) { principal ->
            try {
                ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first())
                ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
            } catch (e: Exception) {
                fail()
            }
            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first())

        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete ticketTagRelation with User as ProjectObserver should fail`() {
        withUser(USER_ID) { principal ->
            ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G1.first(), principal)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete  ticketTagRelation with wrong id should fail`() {
        withUser(ADMIN_ID) { principal ->
            ticketTagRelationController.deleteTicketTagRelation(UUID.fromString("00000000-0000-0000-0000-000000000000"), TAG_AOU_AUO_G1.first(), principal)
        }
    }

    @Test(expected = AccessDeniedException::class)
    fun `delete ticketTagRelation with Observer as ProjectObserver should fail`() {
        withUser(OBSERVER_ID) { principal ->
            ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete ticketTagRelation with Observer as ProjectAdmin should succeed`() {
        withUser(OBSERVER_ID) { principal ->
            try {
                ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())
                ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
            } catch (e: Exception) {
                fail()
            }
            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_UOA_G1.first())

        }

    }

    @Test(expected = NotFoundException::class)
    fun `delete ticketTagRelation with Observer as ProjectUser should succeed`() {
        withUser(OBSERVER_ID) { principal ->
            try {
                ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first(), principal)
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first())
                ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first(), principal)
            } catch (e: Exception) {
                fail()
            }
            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first())


        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete ticketTagRelation with Admin as ProjectObserver should succeed`() {
        withUser(ADMIN_ID) { principal ->
            try {
                ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())
                ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first(), principal)
            } catch (e: Exception) {
                fail()
            }
            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_OAU_0_ID, TAG_AOU_OAU_G1.first())

        }

    }

    @Test(expected = NotFoundException::class)
    fun `delete ticketTagRelation with Admin as ProjectAdmin should succeed`() {
        withUser(ADMIN_ID) { principal ->
            try {
                ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first(), principal)
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first())
                ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first(), principal)
            } catch (e: Exception) {
                fail()
            }
            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_AUO_2_ID, TAG_AOU_AUO_G2.first())


        }
    }

    @Test(expected = NotFoundException::class)
    fun `delete ticketTagRelation with Admin as ProjectUser should succeed`() {
        withUser(ADMIN_ID) { principal ->
            try {
                ticketTagRelationController.setTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
                ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first())
                ticketTagRelationController.deleteTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first(), principal)
            } catch (e: Exception) {
                fail()
            }
            ticketTagRelationController.getTicketTagRelation(TICKET_PROJECT_AOU_UOA_2_ID, TAG_AOU_UOA_G1.first())


        }
    }
}

