package io.ticktag.persistence.timecategory

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TimeCategory
import java.util.*

@TicktagRepository
interface TimeCategoryRepository : TicktagCrudRepository<TimeCategory, UUID> {
    fun findByNormalizedNameAndProjectId(normalizedName: String, projectId: UUID): TimeCategory?
    fun findByProjectIdAndDisabled(projectId: UUID, disabled: Boolean): List<TimeCategory>
}