package io.ticktag.service.comment.dto

import java.time.Duration
import java.util.*

sealed class CommentCommand {
    class Time(val duration: Duration, val category: String) : CommentCommand()
    class Assign(val user: String, val tag: String) : CommentCommand()
    class Unassign(val user: String, val tag: String?) : CommentCommand()
    class Close() : CommentCommand()
    class Reopen() : CommentCommand()
    class Tag(val tag: String) : CommentCommand()
    class Untag(val tag: String) : CommentCommand()
    class Est(val duration: Duration) : CommentCommand()
}
