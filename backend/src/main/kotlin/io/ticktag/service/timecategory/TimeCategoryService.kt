package io.ticktag.service.timecategory

import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.TimeCategoryResult
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import java.util.*

interface TimeCategoryService {
    fun getTimeCategory(id: UUID): TimeCategoryResult
    fun listProjectTimeCategories(projectId: UUID): List<TimeCategoryResult>
    fun createTimeCategory(projectId: UUID, timeCategory: CreateTimeCategory): TimeCategoryResult
    fun deleteTimeCategory(id: UUID)
    fun updateTimeCategory(id: UUID, timeCategory: UpdateTimeCategory): TimeCategoryResult
}