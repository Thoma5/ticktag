package io.ticktag.restinterface.timecategory.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.timecategory.schema.CreateTimeCategoryRequestJson
import io.ticktag.restinterface.timecategory.schema.TimeCategoryJson
import io.ticktag.restinterface.timecategory.schema.TimeCategorySort
import io.ticktag.restinterface.timecategory.schema.UpdateTimeCategoryRequestJson
import io.ticktag.service.timecategory.TimeCategoryService
import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/timecategory")
@Api(tags = arrayOf("timecategory"), description = "time categories")
open class TimeCategoryController @Inject constructor(
        private val timeCategoryService: TimeCategoryService
) {
    @GetMapping
    open fun listTimeCategories(@RequestParam(name = "page", defaultValue = "0", required = false) page: Int,
                                @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                                @RequestParam(name = "order", defaultValue = "NAME", required = false) order: TimeCategorySort,
                                @RequestParam(name = "asc", defaultValue = "true", required = false) asc: Boolean,
                                @RequestParam(name = "usage", defaultValue = "false", required = false) usage: Boolean,
                                @RequestParam(name = "name", defaultValue = "", required = false) name: String
    ): List<TimeCategoryJson> {
        val ascOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        val sortOrder = Sort.Order(ascOrder, order.fieldName).ignoreCase()
        val pageRequest = PageRequest(page, size, Sort(sortOrder))

        return if (usage) {
            timeCategoryService.listTimeCategoriesWithUsage(name, pageRequest).map(::TimeCategoryJson)
        } else {
            timeCategoryService.listTimeCategories(name, pageRequest).map(::TimeCategoryJson)
        }
    }

    @GetMapping(value = "/projects/{pId}") //TODO: Either put this method to projects or do something else since this isn't RESTful
    open fun listProjectTimeCategories(@PathVariable(name = "pId") pId: UUID,
                                       @RequestParam(name = "page", defaultValue = "0", required = false) page: Int,
                                       @RequestParam(name = "size", defaultValue = "50", required = false) size: Int,
                                       @RequestParam(name = "order", defaultValue = "NAME", required = false) order: TimeCategorySort,
                                       @RequestParam(name = "asc", defaultValue = "true", required = false) asc: Boolean,
                                       @RequestParam(name = "usage", defaultValue = "false", required = false) usage: Boolean,
                                       @RequestParam(name = "name", defaultValue = "", required = false) name: String
    ): List<TimeCategoryJson> {
        val ascOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        val sortOrder = Sort.Order(ascOrder, order.fieldName).ignoreCase()
        val pageRequest = PageRequest(page, size, Sort(sortOrder))

        return if (usage) {
            timeCategoryService.listProjectTimeCategoriesWithUsage(pId, name, pageRequest).map(::TimeCategoryJson)
        } else {
            timeCategoryService.listProjectTimeCategories(pId, name, pageRequest).map(::TimeCategoryJson)
        }
    }

    @GetMapping(value = "/{id}")
    open fun getTimeCategory(@PathVariable(name = "id") id: UUID,
                             @RequestParam(name = "usage", defaultValue = "false", required = false) usage: Boolean): TimeCategoryJson {
        return if (usage) {
            TimeCategoryJson(timeCategoryService.getTimeCategoryWithUsage(id))
        } else {
            TimeCategoryJson(timeCategoryService.getTimeCategory(id))
        }
    }

    @PostMapping
    open fun createTimeCategory(@RequestBody req: CreateTimeCategoryRequestJson): TimeCategoryJson {
        val timeCategory = timeCategoryService.createTimeCategory(CreateTimeCategory(req.pId, req.name))
        return TimeCategoryJson(timeCategory)
    }

    @DeleteMapping(value = "/{id}")
    open fun deleteTimeCategory(@PathVariable(name = "id") id: UUID) {
        timeCategoryService.deleteTimeCategory(id)
    }

    @PutMapping(value = "/{id}")
    open fun updateTimeCategory(@PathVariable(name = "id") id: UUID,
                                @RequestBody req: UpdateTimeCategoryRequestJson): TimeCategoryJson {
        val timeCategory = timeCategoryService.updateTimeCategory(id, UpdateTimeCategory(req.name))
        return TimeCategoryJson(timeCategory)
    }
}