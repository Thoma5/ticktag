package io.ticktag.restinterface.timecategory.schema

import java.util.*

data class CreateTimeCategoryRequestJson(
        val pId: UUID,
        val name: String
)

