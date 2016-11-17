package io.ticktag.restinterface.comment.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.comment.schema.CommentResultJson
import io.ticktag.restinterface.comment.schema.CreateCommentRequestJson
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.service.CommentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.inject.Inject


@TicktagRestInterface
@RequestMapping("/comments")
@Api(tags = arrayOf("comments"), description = "comments management")
open class CommentController @Inject constructor(
        private val commentService: CommentService
){

    @GetMapping
    open fun listComments(): List<CommentResultJson> {
        return commentService.listComments().map(::CommentResultJson)
    }
    @PostMapping
    open fun createComment(@RequestBody req: CreateCommentRequestJson): CommentResultJson {
        val comment = commentService.createComment(createComment = CreateComment(req.text, req.userID, req.ticketID))
        return CommentResultJson(comment)
    }
}