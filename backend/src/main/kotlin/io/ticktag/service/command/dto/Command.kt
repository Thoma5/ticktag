package io.ticktag.service.command.dto

import io.ticktag.util.PositiveDuration
import java.time.Duration
import java.time.Instant
import javax.validation.constraints.DecimalMin

sealed class Command {
    class Time(@field:PositiveDuration val duration: Duration, val category: String) : Command()
    class Assign(val user: String, val tag: String) : Command()
    class Unassign(val user: String, val tag: String?) : Command()
    class Close : Command()
    class Reopen : Command()
    class Tag(val tag: String) : Command()
    class Untag(val tag: String) : Command()
    class Est(@field:PositiveDuration val duration: Duration) : Command()
    class Sp(@field:DecimalMin("0") val sp: Int) : Command()
    class Due(val date: Instant) : Command()
    class RefTicket(val ticketNumber: Int) : Command()
}
