package io.ticktag.restinterface

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import io.ticktag.ApplicationProperties
import io.ticktag.service.NotFoundException
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
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
@ComponentScan("io.ticktag.restinterface")
@EnableWebMvc
open class RestConfig : WebMvcConfigurerAdapter() {
    @Bean
    open fun restRequestLoggingAspect(props: ApplicationProperties) = RestRequestLoggingAspect(props)

    @Bean
    open fun objectMapper(): ObjectMapper {
        val jacksonMessageConverter = MappingJackson2HttpMessageConverter()

        val mapper = jacksonMessageConverter.objectMapper
                .registerModule(ParameterNamesModule())
                .registerModule(Jdk8Module())
                .registerModule(JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        return mapper
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>?) {
        converters?.add(MappingJackson2HttpMessageConverter(objectMapper()))
    }
}

@ControllerAdvice
open class RestExceptionHandlers {
    @ExceptionHandler(TicktagValidationException::class)
    open fun handleValidationError(ex: TicktagValidationException): ResponseEntity<List<ValidationErrorJson>> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.errros.map {
            when (it.detail) {
                is ValidationErrorDetail.Size ->
                    ValidationErrorJson(it.field, "size", sizeInfo = ValidationErrorSizeJson(it.detail.min, it.detail.max))
                is ValidationErrorDetail.Pattern ->
                    ValidationErrorJson(it.field, "pattern", patternInfo = ValidationErrorPatternJson(it.detail.regex))
                is ValidationErrorDetail.Other ->
                    ValidationErrorJson(it.field, "other", otherInfo = ValidationErrorOtherJson(it.detail.name))
                is ValidationErrorDetail.Unknown ->
                    ValidationErrorJson(it.field, "unknown")
            }
        })
    }

    @ExceptionHandler(NotFoundException::class)
    open fun handleNotFound(ex: NotFoundException): ResponseEntity<Map<Any, Any>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(emptyMap())
    }
}

@Order(100)
@Aspect
class RestRequestLoggingAspect(private val props: ApplicationProperties) {
    companion object {
        private val LOG = LoggerFactory.getLogger(RestRequestLoggingAspect::class.java)
        private const val MAX_CLASS_NAME_LENGTH = 30
    }

    @Around("@within(io.ticktag.TicktagRestInterface)")
    fun around(pjp: ProceedingJoinPoint): Any? {
        val signature = pjp.signature
        val name = if (signature is MethodSignature) {
            "${ClassUtils.getAbbreviatedName(pjp.target.javaClass, MAX_CLASS_NAME_LENGTH)}.${signature.name}(...)"
        } else {
            "???"
        }

        LOG.info("=== REST request to $name ===")
        val start = System.nanoTime()
        try {
            val result = pjp.proceed()
            if (props.httpSlow) {
                Thread.sleep(1000 + (Math.random() * 500).toLong())
            }
            return result
        } finally {
            val end = System.nanoTime()
            LOG.info("=== REST request processed in ${(end - start) / 1000000} ms ===")
        }
    }
}
