package io.ticktag.service.timecategory

import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.TimeCategoryResult
import io.ticktag.service.timecategory.dto.TimeCategoryWithUsageResult
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import org.springframework.data.domain.Pageable
import java.util.*

interface TimeCategoryService {
    fun getTimeCategory(id: UUID): TimeCategoryResult
    fun getTimeCategoryWithUsage(id: UUID): TimeCategoryWithUsageResult
    fun listTimeCategories(name: String, pageable: Pageable): List<TimeCategoryResult>
    fun listTimeCategoriesWithUsage(name: String, pageable: Pageable): List<TimeCategoryWithUsageResult>
    fun listProjectTimeCategories(pId: UUID, name: String, pageable: Pageable): List<TimeCategoryResult>
    fun listProjectTimeCategoriesWithUsage(pId: UUID, name: String, pageable: Pageable): List<TimeCategoryWithUsageResult>
    fun createTimeCategory(timeCategory: CreateTimeCategory): TimeCategoryResult
    fun deleteTimeCategory(id: UUID)
    fun updateTimeCategory(id: UUID, timeCategory: UpdateTimeCategory): TimeCategoryResult
}