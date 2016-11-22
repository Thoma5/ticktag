package io.ticktag.service.timecategory.services

import io.ticktag.TicktagService
import io.ticktag.library.unicode.NameNormalizationLibrary
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.TimeCategory
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.service.*
import io.ticktag.service.timecategory.TimeCategoryService
import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.TimeCategoryResult
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class TimeCategoryServiceImpl @Inject constructor(
        private val timeCategories: TimeCategoryRepository,
        private val projects: ProjectRepository,
        private val nameNormalizationLibrary: NameNormalizationLibrary
) : TimeCategoryService {

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun getTimeCategory(id: UUID): TimeCategoryResult {
        val timeCategory = timeCategories.findOne(id) ?: throw NotFoundException()
        return TimeCategoryResult(timeCategory)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun getTimeCategoryCount(): Int {
        return timeCategories.count()
    }


    @PreAuthorize(AuthExpr.ADMIN)
    override fun listTimeCategories(name: String, pageable: Pageable): List<TimeCategoryResult> {
        return timeCategories.findByNameContainingIgnoreCase(name, pageable).content.map(::TimeCategoryResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_ADMIN)
    override fun createTimeCategory(timeCategory: CreateTimeCategory): TimeCategoryResult {
        val project = projects.findOne(timeCategory.projectId) ?: throw NotFoundException()

        val normalizedName = nameNormalizationLibrary.normalize(timeCategory.name)
        if (timeCategories.findByNormalizedNameAndProjectId(normalizedName, project.id) != null) {
            throw TicktagValidationException(listOf(ValidationError("createTimeCategory.name", ValidationErrorDetail.Other("inuse"))))
        }

        val newTimeCategory = TimeCategory.create(timeCategory.name, normalizedName, project)
        timeCategories.insert(newTimeCategory)
        return TimeCategoryResult(newTimeCategory)
    }

    @PreAuthorize(AuthExpr.PROJECT_ADMIN)
    override fun deleteTimeCategory(id: UUID) {
        val timeCategoryToDelete = timeCategories.findOne(id) ?: throw NotFoundException()
        timeCategories.delete(timeCategoryToDelete)
    }

    @PreAuthorize(AuthExpr.PROJECT_ADMIN)
    override fun updateTimeCategory(id: UUID, timeCategory: UpdateTimeCategory): TimeCategoryResult {
        val timeCategoryToUpdate = timeCategories.findOne(id) ?: throw NotFoundException()
        if (timeCategory.name != null) {
            val normalizedName = nameNormalizationLibrary.normalize(timeCategory.name)
            val foundTag = timeCategories.findByNormalizedNameAndProjectId(normalizedName, timeCategoryToUpdate.project.id)
            if (foundTag != null && foundTag.id != timeCategoryToUpdate.id) {
                throw TicktagValidationException(listOf(ValidationError("updateTimeCategory.name", ValidationErrorDetail.Other("inuse"))))
            }
            timeCategoryToUpdate.name = timeCategory.name
            timeCategoryToUpdate.normalizedName = normalizedName
        }
        return TimeCategoryResult(timeCategoryToUpdate)
    }

}