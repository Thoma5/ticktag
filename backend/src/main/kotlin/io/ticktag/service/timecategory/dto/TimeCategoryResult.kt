package io.ticktag.service.timecategory.dto

import io.ticktag.persistence.ticket.entity.TimeCategory
import java.util.*

data class TimeCategoryResult(
        val id: UUID,
        val projectId: UUID,
        val name: String,
        val normalizedName: String
) {
    constructor(t: TimeCategory) : this(id = t.id, projectId = t.project.id, name = t.name, normalizedName = t.normalizedName)
}


