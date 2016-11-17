package io.ticktag.restinterface.comment.schema

import java.util.*


class CreateCommentRequestJson(
        val text: String,
        val ticketID: UUID
) {

}