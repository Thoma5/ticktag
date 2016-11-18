package io.ticktag.restinterface.timecategory.schema

import java.util.*

data class CreateTimeCategoryRequestJson(
        val projectId: UUID,
        val name: String
)

