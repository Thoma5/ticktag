package io.ticktag.restinterface

data class ValidationErrorSizeJson(
        val min: Int,
        val max: Int
)

data class ValidationErrorPatternJson(
        val pattern: String
)

data class ValidationErrorOtherJson(
        val name: String
)

data class ValidationErrorJson(
        val field: String,
        val type: String,
        val patternInfo: ValidationErrorPatternJson? = null,
        val sizeInfo: ValidationErrorSizeJson? = null,
        val otherInfo: ValidationErrorOtherJson? = null
)
