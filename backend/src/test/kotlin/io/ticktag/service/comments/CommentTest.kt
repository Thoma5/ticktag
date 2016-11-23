package io.ticktag.service.comments

import io.ticktag.BaseTest
import io.ticktag.restinterface.comment.controllers.CommentController
import io.ticktag.service.NotFoundException
import io.ticktag.service.ServiceBaseTest
import io.ticktag.service.TicktagValidationException
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.dto.UpdateComment
import io.ticktag.service.comment.service.CommentService
import io.ticktag.service.ticket.service.TicketService
import org.junit.Test
import org.springframework.test.context.web.WebAppConfiguration
import java.util.*
import javax.inject.Inject


class CommentTest : ServiceBaseTest() {

    @Inject lateinit private var commentService: CommentService

    @Inject lateinit private var ticketService: TicketService


    @Test
    fun test_check_updateComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            commentService.updateComment(comment_id, UpdateComment("test",null))
            val comment = commentService.getComment(comment_id) ?: throw NotFoundException()
            assert(comment.text == "test")
        }
    }

    @Test
    fun test_check_getComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            val comment = commentService.getComment(comment_id) ?: throw NotFoundException()
            assert(comment.id == comment_id)
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
            commentService.updateComment(comment_id, UpdateComment("test", emptyList()))

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
            assert(createResult.mentionedTicketIds.size==1)
            assert(createResult.mentionedTicketIds.contains(referenceTicketId))
            val referenceTicket = ticketService.getTicket(referenceTicketId)
            assert(referenceTicket.mentoningCommentIds.contains(createResult.id))//check if Comment ID is referencedTicket Test may fail if Tickets were reworked
        }
    }
}