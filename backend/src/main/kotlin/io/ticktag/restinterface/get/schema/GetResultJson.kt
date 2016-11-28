package io.ticktag.restinterface.get.schema

import io.ticktag.restinterface.statistic.schema.TicketProgressResultJson
import io.ticktag.restinterface.ticket.schema.TicketResultJson
import io.ticktag.restinterface.user.schema.UserResultJson
import java.util.*

data class GetResultJson(
        val users: Map<UUID, UserResultJson>,
        val tickets: Map<UUID, TicketResultJson>,
        val ticketStatistics: Map<UUID, TicketProgressResultJson>
)
