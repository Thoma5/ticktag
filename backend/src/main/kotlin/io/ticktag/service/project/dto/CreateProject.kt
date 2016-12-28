package io.ticktag.service.project.dto

import java.util.*
import javax.validation.constraints.Size

data class CreateProject(
        @field:Size(min = 3, max = 30) val name: String,
        @field:Size(min = 3, max = 255) val description: String,
        val icon: String?
)