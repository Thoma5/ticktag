package io.ticktag.restinterface.comment.schema

import java.util.*


data class CreateCommentRequestJson(
        val text: String,
        val ticketId: UUID,
        val commands: List<CommandJson>
)
