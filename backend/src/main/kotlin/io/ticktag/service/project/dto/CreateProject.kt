package io.ticktag.service.project.dto

import java.util.*
import javax.validation.constraints.Size


data class CreateProject(
        @field:Size(min = 3, max = 255) val name: String,
        @field:Size(min = 3, max = 30) val description: String,
        @field:Size(max = 204800) val icon: ByteArray
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