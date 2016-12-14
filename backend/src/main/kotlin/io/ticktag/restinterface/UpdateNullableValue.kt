package io.ticktag.restinterface

// This distinction is made for Jackson and the TypeScript code generator
data class UpdateNullableValueJson<out T>(val value: T?)
data class UpdateNotnullValueJson<out T>(val value: T)
