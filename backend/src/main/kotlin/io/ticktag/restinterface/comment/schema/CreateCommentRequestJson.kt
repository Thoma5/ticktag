package io.ticktag.restinterface.comment.schema

import java.util.*

/**
 * Created by stefandraskovits on 17/11/2016.
 */
class CreateCommentRequestJson (
        val text: String,
        val userID: UUID, //TODO: eigentlich unnötig
        val ticketID: UUID
){

}