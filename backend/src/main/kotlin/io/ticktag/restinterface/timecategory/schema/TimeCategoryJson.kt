package io.ticktag.restinterface.timecategory.schema

import io.ticktag.service.timecategory.dto.TimeCategoryResult
import java.util.*

data class TimeCategoryJson(
        val id: UUID,
        val projectId: UUID,
        val name: String
) {
    constructor(t: TimeCategoryResult) : this(id = t.id, projectId = t.projectId, name = t.name)
}



