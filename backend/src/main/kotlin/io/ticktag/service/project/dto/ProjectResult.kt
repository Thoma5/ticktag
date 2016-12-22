package io.ticktag.service.project.dto

import io.ticktag.persistence.project.entity.Project
import java.util.*

data class ProjectResult(
        val id: UUID,
        val name: String,
        val description: String,
        val creationDate: Date,
        val iconMimeInfo: String?,
        val icon: String?

) {
    constructor(p: Project) : this(id = p.id, name = p.name, description = p.description, creationDate = p.creationDate, iconMimeInfo= p.iconMimeInfo, icon = if(p.icon == null) null else Base64.getEncoder().encodeToString(p.icon))
    constructor(id: UUID, name: String, description: String, creationDate: Date, iconMimeInfo: String?, icon: ByteArray?) : this(id = id, name = name, description = description, creationDate = creationDate, iconMimeInfo= iconMimeInfo, icon = if(icon == null) null else Base64.getEncoder().encodeToString(icon))
}

