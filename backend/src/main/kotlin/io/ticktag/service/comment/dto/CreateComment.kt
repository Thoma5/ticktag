package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Instant
import java.util.*


class CreateComment (
        val id: UUID,
        val text: String,
        val userID: UUID, //TODO: eigentlich unnötig
        val ticketID: UUID
){

}