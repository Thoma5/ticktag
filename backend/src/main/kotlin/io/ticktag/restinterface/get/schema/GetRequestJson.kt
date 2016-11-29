package io.ticktag.restinterface.get.schema

import java.util.*

data class GetRequestJson(
        val userIds: List<UUID>?,
        val ticketIds: List<UUID>?,
        val ticketIdsForStatistic: List<UUID>?
)
