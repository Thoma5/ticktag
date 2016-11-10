package io.ticktag.restinterface.project.schema

import io.ticktag.service.project.dto.CreateProject
import java.util.*


data class CreateProjectRequestJson(
        val name: String,
        val description: String,
        val icon: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        return super.equals(other) && other is CreateProject && Arrays.equals(other.icon, this.icon)
    }

    override fun hashCode(): Int {
        return super.hashCode() + Arrays.hashCode(icon)
    }

    override fun toString(): String {
        return super.toString()
    }
}