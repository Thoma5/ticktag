package io.ticktag.restinterface.project.schema


enum class ProjectSort(val columnName: String) {
    NAME("name"),
    CREATION_DATE("creation_date")
}