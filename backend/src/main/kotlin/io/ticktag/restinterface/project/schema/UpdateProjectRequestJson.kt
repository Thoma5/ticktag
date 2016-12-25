package io.ticktag.restinterface.project.schema


data class UpdateProjectRequestJson(
        val name: String?,
        val description: String?,
        val disabled: Boolean?,
        val icon: String?
)