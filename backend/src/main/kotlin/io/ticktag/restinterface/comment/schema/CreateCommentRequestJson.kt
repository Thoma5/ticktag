package io.ticktag.restinterface.comment.schema

import java.util.*


data class CreateCommentRequestJson(
        val text: String,
        val ticketID: UUID
) {

}