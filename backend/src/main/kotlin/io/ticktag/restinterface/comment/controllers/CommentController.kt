package io.ticktag.restinterface.comment.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.comment.schema.CommentResultJson
import io.ticktag.restinterface.comment.schema.CreateCommentRequestJson
import io.ticktag.service.NotFoundException
import io.ticktag.service.Principal
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.service.CommentService
import org.springframework.http.ResponseEntity
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
    open fun listComments(@RequestParam(name = "ticketId") req: UUID): List<CommentResultJson> {
        return commentService.listCommentsForTicket(req).map(::CommentResultJson)
    }

    @GetMapping(value = "/{id}")
    open fun getComment(@PathVariable(name = "id") id: UUID): CommentResultJson {
        return CommentResultJson(commentService.getComment(id) ?: throw NotFoundException())
    }


    @PostMapping
    open fun createComment(@RequestBody req: CreateCommentRequestJson,
                           @AuthenticationPrincipal principal: Principal): ResponseEntity<CommentResultJson> {
        val commands = req.commands.map({
            it.toCommentCommand() ?: return ResponseEntity.badRequest().body(null)
        })
        val createComment = CreateComment(req.text, req.ticketId, commands)
        val comment = commentService.createComment(createComment, principal, req.ticketId)
        return ResponseEntity.ok(CommentResultJson(comment))
    }
}