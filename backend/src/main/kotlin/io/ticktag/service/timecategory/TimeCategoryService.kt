package io.ticktag.service.timecategory

import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.TimeCategoryResult
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import org.springframework.data.domain.Pageable
import java.util.*

interface TimeCategoryService {
    fun getTimeCategory(id: UUID): TimeCategoryResult
    fun getTimeCategoryCount(): Int
    fun listTimeCategories(name: String, pageable: Pageable): List<TimeCategoryResult>
    fun createTimeCategory(timeCategory: CreateTimeCategory): TimeCategoryResult
    fun deleteTimeCategory(id: UUID)
    fun updateTimeCategory(id: UUID, timeCategory: UpdateTimeCategory): TimeCategoryResult
}