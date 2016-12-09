package io.ticktag.persistence.ticket.dto

import java.time.Instant
import java.util.*


data class TicketFilter(val project: UUID, val number: Int?, val title: String?, val tags: List<String>?,
                        val users: List<String>?, val progressOne: Float?, val progressTwo: Float?,
                        val progressGreater: Boolean?, val dueDateFrom: Instant?, val dueDateTwo: Instant?,
                        val dueDateGreater: Boolean?, val open: Boolean?)