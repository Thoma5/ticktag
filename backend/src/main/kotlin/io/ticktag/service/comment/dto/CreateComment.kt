package io.ticktag.service.comment.dto

import io.ticktag.persistence.ticket.entity.Ticket
import io.ticktag.persistence.user.entity.User
import java.time.Instant
import java.util.*


class CreateComment (
        val text: String,
        val ticketID: UUID
){

}