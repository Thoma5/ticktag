package io.ticktag.restinterface.comment.schema

import io.ticktag.service.command.dto.Command
import java.time.Duration

data class CommandJson(
        val cmd: String,
        val minutes: Long?,
        val category: String?,
        val user: String?,
        val tag: String?,
        val ticket: Int?
) {
    fun toCommentCommand(): Command? {
        return when (cmd) {
            "time" -> Command.Time(Duration.ofMinutes(minutes ?: return null), category ?: return null)
            "assign" -> Command.Assign(user ?: return null, tag ?: return null)
            "unassign" -> Command.Unassign(user ?: return null, tag)
            "close" -> Command.Close()
            "reopen" -> Command.Reopen()
            "tag" -> Command.Tag(tag ?: return null)
            "untag" -> Command.Untag(tag ?: return null)
            "est" -> Command.Est(Duration.ofMinutes(minutes ?: return null))
            "refTicket" -> Command.RefTicket(ticket ?: return null)
            else -> null
        }
    }
}
