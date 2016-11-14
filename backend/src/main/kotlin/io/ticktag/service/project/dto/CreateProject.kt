package io.ticktag.service.project.dto

import javax.validation.constraints.Size

data class CreateProject(
        @field:Size(min = 3, max = 255) val name: String,
        @field:Size(min = 3, max = 30) val description: String,
        @field:Size(max = 204800) val icon: ByteArray?
)