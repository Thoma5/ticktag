package io.ticktag.service

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import javax.validation.ConstraintViolation
import javax.validation.Valid
import javax.validation.Validator
import javax.validation.constraints.Size

@Configuration
@ComponentScan(basePackages = arrayOf("io.ticktag.service"))
open class ServiceConfig {
    @Bean
    open fun validateServiceAspect(validator: Validator): ValidateServiceAspect {
        return ValidateServiceAspect(validator)
    }
}

@Order(500)
@Aspect
class ValidateServiceAspect(private val validator: Validator) {
    @Before("@within(io.ticktag.TicktagService)")
    fun before(jp: JoinPoint) {
        val results = mutableMapOf<String, Set<ConstraintViolation<Any>>>()

        if (jp is MethodInvocationProceedingJoinPoint) {
            val signature = jp.signature
            if (signature is MethodSignature) {  // What else?
                val method = signature.method
                var i = 0
                while (i < method.parameterCount) {
                    if (method.parameters[i].isAnnotationPresent(Valid::class.java)) {
                        val name = method.parameters[i].getAnnotation(ValidationName::class.java)?.value ?: method.parameters[i].type.simpleName.toCamelCase()
                        val errors = validator.validate(jp.args[i])
                        if (errors.isNotEmpty()) {
                            results[name] = errors
                        }
                    }
                    ++i
                }
            }
        }

        if (results.isNotEmpty()) {
            val errors = mutableListOf<ValidationError>()
            for ((property, violations) in results) {
                for (violation in violations) {
                    val ann = violation.constraintDescriptor.annotation
                    val detail = when(ann) {
                        is Size -> ValidationErrorDetail.Size(ann.min, ann.max)
                        is Length -> ValidationErrorDetail.Size(ann.min, ann.max)
                        is NotEmpty -> ValidationErrorDetail.Size(1, Int.MAX_VALUE)
                        else -> ValidationErrorDetail.Unknown
                    }
                    errors.add(ValidationError("$property.${violation.propertyPath}", detail))
                }
            }
            throw TicktagValidationException(errors)
        }
    }
}

private fun String.toCamelCase(): String = substring(0, 1).toLowerCase() + substring(1)
