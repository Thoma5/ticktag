package io.ticktag.restinterface.project.schema

import io.ticktag.service.project.dto.ProjectResult
import java.util.*

class ProjectResultJson(
        val id: UUID,
        val name: String,
        val description: String,
        val creationDate: Date,
        val icon: ByteArray?

) {
    constructor(p: ProjectResult) : this(id = p.id, name = p.name, description = p.description, creationDate = p.creationDate, icon = p.icon)

    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is ProjectResult && Arrays.equals(other.icon, this.icon)
    }

    override fun hashCode(): Int {
        return super.hashCode() + Arrays.hashCode(icon)
    }

    override fun toString(): String {
        return super.toString()
    }
}