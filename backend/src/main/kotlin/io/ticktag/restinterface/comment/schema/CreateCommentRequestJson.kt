package io.ticktag.restinterface.comment.schema

import io.ticktag.service.comment.dto.CreateLoggedTimeJson
import java.util.*


data class CreateCommentRequestJson(
        val text: String,
        val ticketId: UUID,
        val mentionedTicketIds: List<UUID>,
        val loggedTime: List<CreateLoggedTimeJson>
) {

}