package io.ticktag.restinterface.timecategory.controllers

import io.swagger.annotations.Api
import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.timecategory.schema.CreateTimeCategoryRequestJson
import io.ticktag.restinterface.timecategory.schema.TimeCategoryJson
import io.ticktag.restinterface.timecategory.schema.UpdateTimeCategoryRequestJson
import io.ticktag.service.timecategory.TimeCategoryService
import io.ticktag.service.timecategory.dto.CreateTimeCategory
import io.ticktag.service.timecategory.dto.UpdateTimeCategory
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/timecategory/")
@Api(tags = arrayOf("timecategory"), description = "time categories")
open class TimeCategoryController @Inject constructor(
        private val timeCategoryService: TimeCategoryService
) {
    @GetMapping
    open fun listProjectTimeCategories(
            @RequestParam(name = "projectId", required = true) projectId: UUID
    ): List<TimeCategoryJson> {
        return timeCategoryService.listProjectTimeCategories(projectId).map(::TimeCategoryJson)
    }

    @GetMapping(value = "/{id}")
    open fun getTimeCategory(@PathVariable(name = "id") id: UUID): TimeCategoryJson {
        return TimeCategoryJson(timeCategoryService.getTimeCategory(id))

    }

    @PostMapping
    open fun createTimeCategory(@RequestBody req: CreateTimeCategoryRequestJson): TimeCategoryJson {
        val timeCategory = timeCategoryService.createTimeCategory(req.projectId, CreateTimeCategory(req.projectId, req.name))
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