package io.ticktag.restinterface.project.schema


data class CreateProjectRequestJson(
        val name: String,
        val description: String,
        val ticketTemplate: String,
        val icon: String?
)