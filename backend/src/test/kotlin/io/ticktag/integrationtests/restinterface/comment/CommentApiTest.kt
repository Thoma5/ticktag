package io.ticktag.restinterface.comment

import io.ticktag.*
import io.ticktag.integrationtests.restinterface.ApiBaseTest
import io.ticktag.restinterface.comment.controllers.CommentController
import io.ticktag.restinterface.comment.schema.CommandJson
import io.ticktag.restinterface.comment.schema.CreateCommentRequestJson
import io.ticktag.restinterface.comment.schema.UpdateCommentRequestJson
import io.ticktag.restinterface.loggedtime.controller.LoggedTimeController
import io.ticktag.restinterface.ticket.controllers.TicketController
import io.ticktag.restinterface.ticketuserrelation.schema.TicketUserRelationResultJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.Duration
import java.util.*
import javax.inject.Inject

private val TEST_TICKET = TICKET_PROJECT_AOU_AUO_2_ID

class CommentApiTest : ApiBaseTest() {

    @Inject lateinit private var commentController: CommentController
    @Inject lateinit private var loggedTimeController: LoggedTimeController
    @Inject lateinit private var ticketController: TicketController

    override fun loadTestData(): List<String> {
        return listOf("sql/testBaseSamples.sql")
    }

    @Test
    fun `updateComment should update a comment`() {
        withUser(ADMIN_ID) { ->

            commentController.updateComment(UpdateCommentRequestJson("test", null, null), COMMENTS_PROJECT1_TICKET1[0])

            val comment = commentController.getComment(COMMENTS_PROJECT1_TICKET1[0])
            assertThat(comment.text, `is`("test"))
        }
    }

    @Test
    fun `getComment should find a comment`() {
        withUser(ADMIN_ID) { ->

            val comment = commentController.getComment(COMMENTS_PROJECT1_TICKET1[0])

            assertThat(comment.id, `is`(COMMENTS_PROJECT1_TICKET1[0]))
        }
    }

    @Test(expected = NotFoundException::class)
    fun `deleteComment should delete a comment`() {
        withUser(ADMIN_ID) { ->

            commentController.deleteComment(COMMENTS_PROJECT1_TICKET1[0])

            commentController.getComment(COMMENTS_PROJECT1_TICKET1[0])
        }
    }


    @Test(expected = NotFoundException::class)
    fun `updateComment should 404 if the comment does not exist`() {
        withUser(ADMIN_ID) { ->

            commentController.updateComment(UpdateCommentRequestJson("test", null, null), UUID(-1, -1))
        }
    }

    @Test(expected = NotFoundException::class)
    fun `getComment should 404 if the commend does not exist`() {
        withUser(ADMIN_ID) { ->

            commentController.getComment(UUID(-1, -1))
        }
    }

    @Test(expected = NotFoundException::class)
    fun `deleteComment should 404 if the comment does not exist`() {
        withUser(ADMIN_ID) { ->

            commentController.deleteComment(UUID(-1, -1))
        }
    }

    @Test
    fun `createComment with a time command should log time`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("time", 90, TIMECAT_CONTENT[0], null, null, null))

            val comment = commentController.getComment(id)
            assertThat(comment.loggedTimeIds.size, `is`(1))
            val time = loggedTimeController.getLoggedTimesForId(comment.loggedTimeIds[0])
            assertThat(time.categoryId, `is`(TIMECAT_PROJECT_AOU_AUO_IDS[0]))
            assertThat(time.time, `is`(Duration.ofMinutes(90)))
        }
    }

    @Test
    fun `createComment with an assign command should assing the user`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("assign", null, null, "obelix", ASSTAG_CONTENT[0], null))

            commentController.getComment(id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.ticketUserRelations, hasItem(TicketUserRelationResultJson(
                    ticketId = TEST_TICKET,
                    assignmentTagId = ASSTAG_PROJECT_AOU_AUO_IDS[0],
                    userId = OBSERVER_ID
            )))
        }
    }

    @Test
    fun `createComment with an unassign command should unassign the user`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("unassign", null, null, "admit", ASSTAG_CONTENT[0], null))

            commentController.getComment(id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.ticketUserRelations.map { it.userId }, not(hasItem(ADMIN_ID)))
        }
    }

    @Test
    fun `createComment with an unassign command without tag should unassign the user`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("unassign", null, null, "admit", null, null))

            commentController.getComment(id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.ticketUserRelations.map { it.userId }, not(hasItem(ADMIN_ID)))
        }
    }

    @Test
    fun `createComment with a close and reopen command should change the open status of the ticket`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("close", null, null, null, null, null))

            commentController.getComment(id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.open, `is`(false))

            val id2 = createCommentCmd(principal, CommandJson("reopen", null, null, null, null, null))

            commentController.getComment(id2)
            val ticket2 = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket2.open, `is`(true))
        }
    }

    @Test
    fun `createComment with a tag command should assign the tag`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("tag", null, null, null, "bug", null))

            commentController.getComment(id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.tagIds, hasItem(UUID.fromString("00000000-0005-0000-0000-000000000102")))
        }
    }

    @Test
    fun `createComment with an untag command should remove the tag`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("untag", null, null, null, "feature", null))

            commentController.getComment(id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.tagIds, not(hasItem(UUID.fromString("00000000-0005-0000-0000-000000000101"))))
        }
    }

    @Test
    fun `createComment with an est command should change the current estimation`() {
        withUser(ADMIN_ID) { principal ->

            val id = createCommentCmd(principal, CommandJson("est", 1234, null, null, null, null))

            commentController.getComment(id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.currentEstimatedTime, `is`(Duration.ofMinutes(1234)))
        }
    }

    @Test
    fun `createComment with references should reference other tickets`() {
        withUser(ADMIN_ID) { principal ->

            val result = commentController.createComment(CreateCommentRequestJson(
                    text = "Text",
                    ticketId = TEST_TICKET,
                    commands = listOf(CommandJson("refTicket", null, null, null, null, 2))
            ), principal)

            commentController.getComment(result.body.id)
            val ticket = ticketController.getTicket(TEST_TICKET)
            assertThat(ticket.referencedTicketIds, hasItem(UUID.fromString("00000000-0003-0000-0000-000000000105")))
        }
    }

    private fun createCommentCmd(principal: Principal, vararg commands: CommandJson): UUID {
        val result = commentController.createComment(CreateCommentRequestJson(
                text = "Text",
                ticketId = TEST_TICKET,
                commands = commands.asList()
        ), principal)
        return result.body.id
    }
}