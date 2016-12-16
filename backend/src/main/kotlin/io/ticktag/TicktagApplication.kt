package io.ticktag

import io.ticktag.library.LibraryConfig
import io.ticktag.persistence.PersistenceConfig
import io.ticktag.restinterface.RestConfig
import io.ticktag.service.ServiceConfig
import io.ticktag.service.UpdateValue
import io.ticktag.service.fallbackadmin.services.FallbackAdminService
import io.ticktag.swaggerinterface.SwaggerConfig
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.hibernate.validator.HibernateValidator
import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.DispatcherServlet
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.Clock
import java.util.*
import javax.servlet.DispatcherType
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true, order = 400)
@Import(PersistenceConfig::class, LibraryConfig::class, ServiceConfig::class)
open class TicktagBaseApplication {
    @Bean
    open fun applicationProperties(): ApplicationProperties {
        return PropertiesLoader().getProperties()
    }

    @Bean
    open fun validatorFactory(): ValidatorFactory {
        return Validation
                .byProvider(HibernateValidator::class.java)
                .configure()
                .addValidatedValueHandler(object : ValidatedValueUnwrapper<UpdateValue<Any?>>() {
                    override fun handleValidatedValue(value: UpdateValue<Any?>?): Any? = value?.value
                    override fun getValidatedValueType(valueType: Type): Type = (valueType as ParameterizedType).actualTypeArguments[0]
                })
                .buildValidatorFactory()
    }

    @Bean
    open fun validator(validatorFactory: ValidatorFactory): Validator = validatorFactory.validator
}

@Configuration
@Import(TicktagBaseApplication::class, RestConfig::class, SwaggerConfig::class)
open class TicktagApplication {
    @Bean
    open fun startupListener(fallbackAdminService: FallbackAdminService): StartupListener {
        return StartupListener(fallbackAdminService)
    }

    @Bean
    open fun clock(): Clock {
        return Clock.systemUTC()
    }
}

fun main(params: Array<String>) {
    val props = PropertiesLoader().getProperties()

    val context = AnnotationConfigWebApplicationContext()
    context.register(TicktagApplication::class.java)

    val contextHandler = ServletContextHandler()
    contextHandler.errorHandler = null
    contextHandler.contextPath = "/"
    contextHandler.addServlet(ServletHolder(DispatcherServlet(context)), "/")
    contextHandler.addEventListener(ContextLoaderListener(context))
    contextHandler.addFilter(FilterHolder(DelegatingFilterProxy("springSecurityFilterChain")), "/*", EnumSet.allOf(DispatcherType::class.java))

    val server = Server(Integer.valueOf(props.httpPort))
    server.handler = contextHandler

    server.start()
    server.join()
}
