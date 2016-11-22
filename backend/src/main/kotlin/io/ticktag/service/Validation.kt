package io.ticktag.service

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ValidationName(val value: String)

data class ValidationError(val field: String, val detail: ValidationErrorDetail)

sealed class ValidationErrorDetail {
    class Size(val min: Int, val max: Int) : ValidationErrorDetail()
    class Pattern(val regex: String) : ValidationErrorDetail()
    class Other(val name: String) : ValidationErrorDetail()
    object Unknown : ValidationErrorDetail()
}

class TicktagValidationException(val errros: List<ValidationError>) : RuntimeException("Service layer validation failed")
