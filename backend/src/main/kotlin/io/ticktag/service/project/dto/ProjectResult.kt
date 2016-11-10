package io.ticktag.service.project.dto

import io.ticktag.persistence.project.entity.Project
import java.util.*

data class ProjectResult(
        val id: UUID,
        val name: String,
        val description: String,
        val creationDate: Date,
        val icon: ByteArray?

) {
    constructor(p: Project) : this(id = p.id, name = p.name, description = p.description, creationDate = p.creationDate, icon = p.icon)
}

