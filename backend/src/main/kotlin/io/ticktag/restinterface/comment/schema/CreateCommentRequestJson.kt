package io.ticktag.restinterface.comment.schema

import io.ticktag.restinterface.loggedtime.schema.CreateLoggedTimeJson
import java.util.*


data class CreateCommentRequestJson(
        val text: String,
        val ticketId: UUID,
        val mentionedTicketIds: List<UUID>,
        val loggedTime: List<CreateLoggedTimeJson>
) {

}