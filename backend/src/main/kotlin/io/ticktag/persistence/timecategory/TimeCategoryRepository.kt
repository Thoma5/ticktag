package io.ticktag.persistence.timecategory

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.ticket.entity.TimeCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

@TicktagRepository
interface TimeCategoryRepository : TicktagCrudRepository<TimeCategory, UUID> {
    fun findByProjectIdAndNameContainingIgnoreCase(pId: UUID, name: String, pageable: Pageable): Page<TimeCategory>
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<TimeCategory>
    fun findByNormalizedNameAndProjectId(normalizedName: String, projectId: UUID): TimeCategory?
}