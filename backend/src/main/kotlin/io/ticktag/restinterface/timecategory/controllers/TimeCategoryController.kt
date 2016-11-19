package io.ticktag.restinterface.timecategory.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.generic.CountJson
import io.ticktag.restinterface.timecategory.schema.CreateTimeCategoryRequestJson
import io.ticktag.restinterface.timecategory.schema.TimeCategoryJson
import io.ticktag.restinterface.timecategory.schema.TimeCategorySort
import io.ticktag.restinterface.timecategory.schema.UpdateTimeCategoryRequestJson
import io.ticktag.service.timecategory.TimeCategoryService
import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
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
                                @RequestParam(name = "name", defaultValue = "", required = false) name: String
    ): Page<TimeCategoryJson> {
        val ascOrder = if (asc) Sort.Direction.ASC else Sort.Direction.DESC
        val sortOrder = Sort.Order(ascOrder, order.fieldName).ignoreCase()
        val pageRequest = PageRequest(page, size, Sort(sortOrder))

        var page = timeCategoryService.listTimeCategories(name, pageRequest)
        var content = page.content.map(::TimeCategoryJson)
        return PageImpl(content, pageRequest, page.totalElements)

    }

    @GetMapping(value = "/{id}")
    open fun getTimeCategory(@PathVariable(name = "id") id: UUID,
                             @RequestParam(name = "usage", defaultValue = "false", required = false) usage: Boolean): TimeCategoryJson {
        return TimeCategoryJson(timeCategoryService.getTimeCategory(id))

    }

    @GetMapping(value = "/count")
    open fun getTimeCategoryCount(): CountJson {
        return CountJson(timeCategoryService.getTimeCategoryCount())

    }

    @PostMapping
    open fun createTimeCategory(@RequestBody req: CreateTimeCategoryRequestJson): TimeCategoryJson {
        val timeCategory = timeCategoryService.createTimeCategory(CreateTimeCategory(req.projectId, req.name))
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