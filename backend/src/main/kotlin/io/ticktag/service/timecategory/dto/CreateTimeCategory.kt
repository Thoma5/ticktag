package io.ticktag.service.timecategory.dto

import java.util.*
import javax.validation.constraints.Size

data class CreateTimeCategory(
        val projectId: UUID,
        @field:Size(min = 3, max = 255) val name: String
)

