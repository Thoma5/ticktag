package io.ticktag.restinterface.project.schema

import io.ticktag.service.project.dto.ProjectResult
import java.util.*

fun createBase64StringWithMimeInfo(iconMimeInfo: String?, icon: String?): String? {
    return if (icon != null && iconMimeInfo != null) {
        "data:$iconMimeInfo;base64,$icon"
    } else {
        null
    }
}

class ProjectResultJson(
        val id: UUID,
        val name: String,
        val description: String,
        val disabled: Boolean,
        val creationDate: Date,
        val icon: String?
) {
    constructor(p: ProjectResult) : this(id = p.id, name = p.name, description = p.description, disabled = p.disabled, creationDate = p.creationDate, icon = createBase64StringWithMimeInfo(p.iconMimeInfo, p.icon))
}