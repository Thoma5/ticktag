package io.ticktag.restinterface.comment

import io.ticktag.service.NotFoundException
import io.ticktag.service.ServiceBaseTest

import io.ticktag.service.TicktagValidationException
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.dto.UpdateComment
import io.ticktag.service.comment.service.CommentService
import io.ticktag.service.ticket.service.TicketService
import org.junit.Assert
import org.junit.Test
import java.util.*
import javax.inject.Inject
import org.junit.Assert.assertThat
import org.junit.Assert.assertEquals
import org.hamcrest.CoreMatchers.*

class CommentTest : ServiceBaseTest() {

    @Inject lateinit private var commentService: CommentService

    @Inject lateinit private var ticketService: TicketService


    @Test
    fun test_check_updateComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            commentService.updateComment(comment_id, UpdateComment("test",null,null))
            val comment = commentService.getComment(comment_id) ?: throw NotFoundException()
            assertEquals(comment.text, "test")
        }
    }

    @Test
    fun test_check_getComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            val comment = commentService.getComment(comment_id) ?: throw NotFoundException()
            assertEquals(comment.id, comment_id)
        }
    }

    @Test(expected = NotFoundException::class)
    fun test_check_deleteComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            commentService.deleteComment(comment_id)
            commentService.getComment(comment_id)
        }
    }


    @Test(expected = NotFoundException::class)
    fun test_check_updateComment_negative() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000001")
        withUser(id) { principal ->
            commentService.updateComment(comment_id, UpdateComment("test", emptyList(),null))

        }
    }

    @Test(expected = NotFoundException::class)
    fun test_check_getComment_negative() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000001")
        withUser(id) { principal ->
            commentService.getComment(comment_id)
        }
    }

    @Test(expected = NotFoundException::class)
    fun test_check_deleteComment_negative() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000001")
        withUser(id) { principal ->
            commentService.deleteComment(comment_id)
        }
    }


    @Test
    fun test_mentioningTickets_positiv() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val ownerTicketId = UUID.fromString("00000000-0003-0000-0000-000000000001")
        val referenceTicketId = UUID.fromString("00000000-0003-0000-0000-000000000002")
        withUser(id) { principal ->
            val createComment = CreateComment("test",ownerTicketId, listOf(referenceTicketId),null)
            val createResult = commentService.createComment(createComment,principal,ownerTicketId)
            assertEquals(createResult.mentionedTicketIds.size,1)
            assertThat(createResult.mentionedTicketIds, `hasItem`(referenceTicketId))
            val referenceTicket = ticketService.getTicket(referenceTicketId)
            assertThat(referenceTicket.mentoningCommentIds, `hasItem`(createResult.id))//check if Comment ID is referencedTicket Test may fail if Tickets were reworked
        }
    }
}