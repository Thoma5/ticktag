package io.ticktag.restinterface

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
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@ComponentScan("io.ticktag.restinterface")
@EnableWebMvc
open class RestConfig {
    @Bean
    open fun restRequestLoggingAspect() = RestRequestLoggingAspect()
}

@Order(10)
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

        LOG.info("========== REST request to $name =========")
        val start = System.nanoTime()
        try {
            return pjp.proceed()
        } finally {
            val end = System.nanoTime()
            LOG.info("========== REST request processed in ${(end - start) / 1000000} ms =========")
        }
    }
}
