package io.ticktag.restinterface

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
    }

    @Around("@within(io.ticktag.TicktagRestInterface)")
    fun around(pjp: ProceedingJoinPoint): Any {
        val signature = pjp.signature
        val name = if (signature is MethodSignature) {
            "${pjp.target.javaClass.canonicalName}.${signature.name}(...)"
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
