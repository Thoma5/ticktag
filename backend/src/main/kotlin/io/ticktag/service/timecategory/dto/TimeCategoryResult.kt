package io.ticktag.service.timecategory.dto

import io.ticktag.persistence.ticket.entity.TimeCategory
import java.util.*

data class TimeCategoryResult(
        val id: UUID,
        val pId: UUID,
        val name: String
) {
    constructor(t: TimeCategory) : this(id = t.id, pId = t.project.id, name = t.name)
}


