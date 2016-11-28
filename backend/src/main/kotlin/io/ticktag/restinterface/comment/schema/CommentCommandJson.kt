package io.ticktag.restinterface.comment.schema

import io.ticktag.service.comment.dto.CommentCommand
import java.time.Duration
import java.util.*

data class CommentCommandJson(
        val cmd: String,
        val minutes: Long?,
        val category: String?,
        val user: String?,
        val tag: String?
) {
    fun toCommentCommand(): CommentCommand? {
        return when (cmd) {
            "time" -> CommentCommand.Time(Duration.ofMinutes(minutes ?: return null), category ?: return null)
            "assign" -> CommentCommand.Assign(user ?: return null, tag ?: return null)
            "unassign" -> CommentCommand.Unassign(user ?: return null, tag)
            "close" -> CommentCommand.Close()
            "reopen" -> CommentCommand.Reopen()
            "tag" -> CommentCommand.Tag(tag ?: return null)
            "untag" -> CommentCommand.Untag(tag ?: return null)
            "est" -> CommentCommand.Est(Duration.ofMinutes(minutes ?: return null))
            else -> null
        }
    }
}
