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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@ComponentScan("io.ticktag.restinterface")
@EnableWebMvc
@EnableSwagger2
open class RestConfig : WebMvcConfigurerAdapter() {
    @Bean
    open fun api(): Docket {
        val apiInfo = ApiInfo("TickTag REST API",
                "TickTag issue tracking API",
                "1.0",
                null,
                Contact(null, null, null),
                null,
                null)
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/")
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }

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
