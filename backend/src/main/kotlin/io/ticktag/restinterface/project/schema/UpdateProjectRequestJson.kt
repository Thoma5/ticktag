package io.ticktag.restinterface.project.schema


data class UpdateProjectRequestJson(
        val name: String?,
        val description: String?,
        val icon: ByteArray?
)