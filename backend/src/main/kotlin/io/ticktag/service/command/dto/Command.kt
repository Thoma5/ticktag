package io.ticktag.service.command.dto

import java.time.Duration

sealed class Command {
    class Time(val duration: Duration, val category: String) : Command()
    class Assign(val user: String, val tag: String) : Command()
    class Unassign(val user: String, val tag: String?) : Command()
    class Close() : Command()
    class Reopen() : Command()
    class Tag(val tag: String) : Command()
    class Untag(val tag: String) : Command()
    class Est(val duration: Duration) : Command()
    class RefTicket(val ticketNumber: Int) : Command()
}
