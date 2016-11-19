package io.ticktag.restinterface.comment;

import io.ticktag.restinterface.ApiBaseTest
import io.ticktag.restinterface.comment.controllers.CommentController
import io.ticktag.restinterface.comment.schema.UpdateCommentRequestJson
import io.ticktag.service.NotFoundException
import org.junit.Test
import java.util.*
import javax.inject.Inject


open class CommentApiTest : ApiBaseTest() {

    @Inject
    lateinit var commentController: CommentController

    @Test
    fun test_getList() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val pid = UUID.fromString("00000000-0002-0000-0000-000000000001")
        withUser(id) { principal ->
            commentController.listComments(pid)
        }
    }

    @Test
    fun test_updateComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            commentController.updateComment(UpdateCommentRequestJson("test"), comment_id)
            val comment = commentController.getComment(comment_id)
            assert(comment.text == "test")
        }
    }

    @Test
    fun test_getComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            val comment = commentController.getComment(comment_id)
            assert(comment.id == comment_id)
        }
    }

    @Test(expected = NotFoundException::class)
    fun test_deleteComment() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000006")
        withUser(id) { principal ->
            commentController.deleteComment(comment_id)
            commentController.getComment(comment_id)
        }
    }


    @Test(expected = NotFoundException::class)
    fun test_updateComment_negative() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000001")
        withUser(id) { principal ->
            commentController.updateComment(UpdateCommentRequestJson("test"), comment_id)
        }
    }

    @Test(expected = NotFoundException::class)
    fun test_getComment_negative() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000001")
        withUser(id) { principal ->
            commentController.getComment(comment_id)
        }
    }

    @Test(expected = NotFoundException::class)
    fun test_deleteComment_negative() {
        val id = UUID.fromString("00000000-0001-0000-0000-000000000001")
        val comment_id = UUID.fromString("00000000-0004-0000-0000-000000000001")
        withUser(id) { principal ->
            commentController.deleteComment(comment_id)
        }
    }
}
