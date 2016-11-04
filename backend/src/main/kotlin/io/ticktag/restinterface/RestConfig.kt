package io.ticktag.restinterface

import io.ticktag.service.TicktagValidationException
import io.ticktag.service.ValidationErrorDetail
import org.apache.commons.lang3.ClassUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@ComponentScan("io.ticktag.restinterface")
@EnableWebMvc
open class RestConfig {
    @Bean
    open fun restRequestLoggingAspect() = RestRequestLoggingAspect()
}

@ControllerAdvice
open class RestMapValidationErrorToJson {
    @ExceptionHandler(TicktagValidationException::class)
    open fun handleValidationError(ex: TicktagValidationException): ResponseEntity<List<ValidationErrorJson>> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.errros.map {
            when (it.detail) {
                is ValidationErrorDetail.Size ->
                    ValidationErrorJson(it.field, "size", sizeInfo = ValidationErrorSizeJson(it.detail.min, it.detail.max))
                is ValidationErrorDetail.Other ->
                    ValidationErrorJson(it.field, "other", otherInfo = ValidationErrorOtherJson(it.detail.name))
                is ValidationErrorDetail.Unknown ->
                    ValidationErrorJson(it.field, "unknown")
            }
        })
    }
}

@Order(100)
@Aspect
class RestRequestLoggingAspect {
    companion object {
        private val LOG = LoggerFactory.getLogger(RestRequestLoggingAspect::class.java)
        private const val MAX_CLASS_NAME_LENGTH = 30
    }

    @Around("@within(io.ticktag.TicktagRestInterface)")
    fun around(pjp: ProceedingJoinPoint): Any {
        val signature = pjp.signature
        val name = if (signature is MethodSignature) {
            "${ClassUtils.getAbbreviatedName(pjp.target.javaClass, MAX_CLASS_NAME_LENGTH)}.${signature.name}(...)"
        } else {
            "???"
        }

        LOG.info("=== REST request to $name ===")
        val start = System.nanoTime()
        try {
            return pjp.proceed()
        } finally {
            val end = System.nanoTime()
            LOG.info("=== REST request processed in ${(end - start) / 1000000} ms ===")
        }
    }
}
