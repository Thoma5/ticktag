package io.ticktag.restinterface

import io.ticktag.service.UpdateValue

// The distinction of both is made for Jackson and the TypeScript code generator

data class UpdateNullableValueJson<out T>(val value: T?) {
    fun toUpdateValue() = UpdateValue(value)
}

data class UpdateNotnullValueJson<out T>(val value: T) {
    fun toUpdateValue() = UpdateValue(value)
}
