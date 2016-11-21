package io.ticktag.persistence.timecategory

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TimeCategory
import java.util.*

@TicktagRepository
interface TimeCategoryRepository : TicktagCrudRepository<TimeCategory, UUID> {
    fun findByProjectId(pId: UUID): List<TimeCategory>
}