package io.ticktag.restinterface

import io.ticktag.service.Principal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.spi.schema.contexts.ModelPropertyContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import kotlin.reflect.jvm.kotlinProperty

@Configuration
@EnableSwagger2
open class SwaggerConfig : WebMvcConfigurerAdapter() {
    @Bean
    open fun api(): Docket {
        val apiInfo = ApiInfo("TickTag REST API",
                "TickTag issue tracking API",
                "1.0",
                null,
                Contact(null, null, null),
                null,
                null)

        val apiKey = ApiKey("X-Authorization", "api_key", "header")

        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo)
                .consumes(setOf("application/json"))
                .produces(setOf("application/json"))
                .securitySchemes(listOf(apiKey))
                .ignoredParameterTypes(Principal::class.java)
                .protocols(setOf("http"))
    }

    @Bean
    open fun kotlinNotNullPlugin(): ModelPropertyBuilderPlugin {
        return object : ModelPropertyBuilderPlugin {
            override fun apply(context: ModelPropertyContext) {
                if (context.beanPropertyDefinition.isPresent) {
                    val bean = context.beanPropertyDefinition.get()
                    if (bean.hasField()) {
                        val property = bean.field.annotated.kotlinProperty
                        if (property != null && !property.returnType.isMarkedNullable) {
                            context.builder.required(true)
                        }
                    }
                }
            }

            override fun supports(delimiter: DocumentationType): Boolean = true
        }
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/swagger/**")
                .addResourceLocations("classpath:/swagger/")

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }
}
