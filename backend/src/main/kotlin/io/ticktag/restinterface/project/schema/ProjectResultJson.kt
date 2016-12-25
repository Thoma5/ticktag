package io.ticktag.restinterface.project.schema

import io.ticktag.service.project.dto.ProjectResult
import java.util.*

class ProjectResultJson(
        val id: UUID,
        val name: String,
        val description: String,
        val disabled: Boolean,
        val creationDate: Date,
        val icon: String?
) {
    constructor(p: ProjectResult) : this(id = p.id, name = p.name, description = p.description, disabled = p.disabled, creationDate = p.creationDate, icon = if(p.icon != null && p.iconMimeInfo!= null) p.iconMimeInfo + "," + p.icon else null)
}