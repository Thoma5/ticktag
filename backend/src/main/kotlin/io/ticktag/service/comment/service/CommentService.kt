package io.ticktag.service.comment.service

import io.ticktag.service.Principal
import io.ticktag.service.comment.dto.CommentResult
import io.ticktag.service.comment.dto.CreateComment
import io.ticktag.service.comment.dto.UpdateComment
import java.util.*


interface CommentService {
    /**
     * List all comments for a specific ticket
     * @param ticketId if there is no ticket with this id an exception will be thrown
     */
    fun listCommentsForTicket(ticketId: UUID): List<CommentResult>

    /**
     * Get a specific Comment with this ID
     * @param commentId if there is no comment with this id an exception will be thrown
     */
    fun getComment(commentId: UUID): CommentResult?

    /**
     * create Comment and store into Database
     * @param createComment properties of a comment encapsulated into a DTO
     * @param principal security principal
     * @param ticketId id of the ticket, where the comment will be added
     */
    fun createComment(createComment: CreateComment, principal: Principal, ticketId: UUID): CommentResult
}