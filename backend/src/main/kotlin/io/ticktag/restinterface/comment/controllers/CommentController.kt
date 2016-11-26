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
import io.ticktag.service.loggedtime.dto.CreateLoggedTime
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
                           @AuthenticationPrincipal principal: Principal): CommentResultJson {
        val comment = commentService.createComment(createComment = CreateComment(req.text, req.ticketId, req.mentionedTicketIds, req.loggedTime.map(::CreateLoggedTime)), principal = principal, ticketId = req.ticketId)
        return CommentResultJson(comment)
    }

    @PutMapping(value = "/{id}")
    open fun updateComment(@RequestBody req: UpdateCommentRequestJson,
                           @PathVariable(name = "id") id: UUID): CommentResultJson {
        val serviceRequest = UpdateComment(req.text, req.mentionedTicketIds, req.loggedTime?.map(::CreateLoggedTime))
        val serviceResult = commentService.updateComment(updateComment = serviceRequest, commentId = id) ?: throw NotFoundException()
        return CommentResultJson(serviceResult)
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteComment(@PathVariable(name = "id") id: UUID) {
        commentService.deleteComment(id)
    }
}