package io.ticktag.service.timecategory

import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.TimeCategoryResult
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import java.util.*

interface TimeCategoryService {
    /**
     * Get a time category, which can be used to tag an activity.
     */
    fun getTimeCategory(id: UUID): TimeCategoryResult

    /**
     * lists all time categories of a project
     */
    fun listProjectTimeCategories(projectId: UUID): List<TimeCategoryResult>

    /**
     * adds a time category to a project
     */
    fun createTimeCategory(projectId: UUID, timeCategory: CreateTimeCategory): TimeCategoryResult

    /**
     * deletes a time category.
     */
    fun deleteTimeCategory(id: UUID)

    /**
     * updates the properties of a time category.
     */
    fun updateTimeCategory(id: UUID, timeCategory: UpdateTimeCategory): TimeCategoryResult
}