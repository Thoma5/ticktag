package io.ticktag

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class TimeMachine : Clock() {
    var now: Instant = Instant.ofEpochSecond(0)

    override fun instant(): Instant {
        return now
    }

    override fun withZone(zone: ZoneId?): Clock {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getZone(): ZoneId {
        throw UnsupportedOperationException("not implemented")
    }
}