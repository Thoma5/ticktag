package io.ticktag.restinterface.comment.schema

import java.util.*


data class UpdateCommentRequestJson(
        val text: String,
        val mentionedTicketIds: List<UUID>?
) {}