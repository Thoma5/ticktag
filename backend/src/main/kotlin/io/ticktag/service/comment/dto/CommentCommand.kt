package io.ticktag.service.comment.dto

import java.time.Duration
import java.util.*

sealed class CommentCommand {
    class Time(val duration: Duration, val category: UUID) : CommentCommand()
    class Assign(val user: UUID, val tag: UUID) : CommentCommand()
    class Unassign(val user: UUID, val tag: UUID?) : CommentCommand()
    class Close() : CommentCommand()
    class Reopen() : CommentCommand()
    class Tag(val tag: UUID) : CommentCommand()
    class Untag(val tag: UUID) : CommentCommand()
    class Est(val duration: Duration) : CommentCommand()
}
