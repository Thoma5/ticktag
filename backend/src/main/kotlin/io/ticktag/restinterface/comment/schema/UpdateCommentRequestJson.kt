package io.ticktag.restinterface.comment.schema

import io.ticktag.restinterface.loggedtime.schema.CreateLoggedTimeJson
import java.util.*


data class UpdateCommentRequestJson(
        val text: String,
        val mentionedTicketIds: List<UUID>?,
        val loggedTime: List<CreateLoggedTimeJson>?
)