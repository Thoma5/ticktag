package io.ticktag.restinterface.comment.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.comment.schema.CommentResultJson
import io.ticktag.restinterface.comment.schema.CreateCommentRequestJson
import io.ticktag.restinterface.comment.schema.UpdateCommentRequestJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.dto.UpdateComment
import io.ticktag.service.comment.service.CommentService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject


@TicktagRestInterface
@RequestMapping("/comments")
@Api(tags = arrayOf("comments"), description = "comments management")
open class CommentController @Inject constructor(
        private val commentService: CommentService
) {

    @GetMapping
    open fun listComments(): List<CommentResultJson> {
        return commentService.listComments().map(::CommentResultJson)
    }


    @GetMapping(value = "/{id}")
    open fun getComment(@PathVariable(name = "id") id: UUID): CommentResultJson {
        return CommentResultJson(commentService.getComment(id) ?: throw NotFoundException())
    }


    @PostMapping
    open fun createComment(@RequestBody req: CreateCommentRequestJson,
                           @AuthenticationPrincipal principal: Principal): CommentResultJson {
        val comment = commentService.createComment(createComment = CreateComment(req.text, req.ticketID), principal = principal)
        return CommentResultJson(comment)
    }

    @PutMapping(value = "/{id}")
    open fun updateComment(@RequestBody req: UpdateCommentRequestJson,
                           @PathVariable(name = "id") id: UUID): CommentResultJson {
        return CommentResultJson(commentService.updateComment(updateComment = UpdateComment(req.text), commentID = id) ?: throw NotFoundException())
    }

    @DeleteMapping(value = "/{id}")
    open fun updateComment(@PathVariable(name = "id") id: UUID) {
        commentService.deleteComment(id)
    }
}