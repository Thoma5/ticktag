package io.ticktag.restinterface.ticket.schema

import io.ticktag.service.ticket.dto.TicketResult
import java.util.*

class TicketResultJson (
    val id: UUID
){
    constructor(t: TicketResult) : this(id = t.id)
}