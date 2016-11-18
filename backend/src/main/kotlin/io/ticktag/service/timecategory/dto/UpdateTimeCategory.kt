package io.ticktag.service.timecategory.dto

import javax.validation.constraints.Size

data class UpdateTimeCategory(
        // Updating Project isn't necessary since timeCategories are always in the domain of a Project
        @field:Size(min = 3, max = 255) val name: String
)

