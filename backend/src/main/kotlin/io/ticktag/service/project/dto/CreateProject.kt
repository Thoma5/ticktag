package io.ticktag.service.project.dto

import javax.validation.constraints.Size

data class CreateProject(
        @field:Size(min = 3, max = 30) val name: String,
        @field:Size(min = 3, max = 255) val description: String,
        @field:Size(min = 0, max = 50000) val ticketTemplate: String,
        val icon: String?
)