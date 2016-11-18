package io.ticktag.service.timecategory.services

import io.ticktag.TicktagService
import io.ticktag.persistence.project.ProjectRepository
import io.ticktag.persistence.ticket.entity.TimeCategory
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.service.AuthExpr
import io.ticktag.service.NotFoundException
import io.ticktag.service.timecategory.TimeCategoryService
import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.TimeCategoryResult
import io.ticktag.service.timecategory.dto.TimeCategoryWithUsageResult
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import java.util.*
import javax.inject.Inject

@TicktagService
open class TimeCategoryServiceImpl @Inject constructor(
        private val timeCategories: TimeCategoryRepository,
        private val projects: ProjectRepository
) : TimeCategoryService {

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun getTimeCategory(id: UUID): TimeCategoryResult {
        val timeCategory = timeCategories.findOne(id) ?: throw NotFoundException()
        return TimeCategoryResult(timeCategory)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun getTimeCategoryWithUsage(id: UUID): TimeCategoryWithUsageResult {
        val timeCategory = timeCategories.findOne(id) ?: throw NotFoundException()
        return TimeCategoryWithUsageResult(timeCategory)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun listTimeCategories(name: String, pageable: Pageable): List<TimeCategoryResult> {
        return timeCategories.findByNameLike("%$name%", pageable).content.map(::TimeCategoryResult)
    }

    @PreAuthorize(AuthExpr.ADMIN)
    override fun listTimeCategoriesWithUsage(name: String, pageable: Pageable): List<TimeCategoryWithUsageResult> {
        return timeCategories.findByNameLike("%$name%", pageable).content.map(::TimeCategoryWithUsageResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun listProjectTimeCategories(pId: UUID, name: String, pageable: Pageable): List<TimeCategoryResult> {
        return timeCategories.findByProjectAndNameLike(pId, "%$name%", pageable).content.map(::TimeCategoryResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_USER)
    override fun listProjectTimeCategoriesWithUsage(pId: UUID, name: String, pageable: Pageable): List<TimeCategoryWithUsageResult> {
        return timeCategories.findByProjectAndNameLike(pId, "%$name%", pageable).content.map(:: TimeCategoryWithUsageResult)
    }

    @PreAuthorize(AuthExpr.PROJECT_ADMIN)
    override fun createTimeCategory(timeCategory: CreateTimeCategory): TimeCategoryResult {
        val project = projects.findOne(timeCategory.pId) ?: throw NotFoundException()
        val timeCategory = TimeCategory.create(timeCategory.name, project)
        timeCategories.insert(timeCategory)
        return TimeCategoryResult(timeCategory)
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
            timeCategoryToUpdate.name = timeCategory.name
        }
        return TimeCategoryResult(timeCategoryToUpdate)
    }

}