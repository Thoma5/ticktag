package io.ticktag

import io.ticktag.library.LibraryConfig
import io.ticktag.persistence.PersistenceConfig
import io.ticktag.restinterface.RestConfig
import io.ticktag.restinterface.SwaggerConfig
import io.ticktag.service.ServiceConfig
import io.ticktag.service.fallbackadmin.services.FallbackAdminService
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.DispatcherServlet
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.servlet.DispatcherType
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

@Configuration
@Import(PersistenceConfig::class, LibraryConfig::class, ServiceConfig::class, RestConfig::class, SwaggerConfig::class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true, order = 400)
open class TicktagApplication {
    @Bean
    open fun applicationProperties(): ApplicationProperties {
        val props = loadProperties()
        return parseProperties(props)
    }

    @Bean
    open fun validatorFactory(): ValidatorFactory = Validation.buildDefaultValidatorFactory()

    @Bean
    open fun validator(validatorFactory: ValidatorFactory): Validator = validatorFactory.validator

    @Bean
    open fun startupListener(fallbackAdminService: FallbackAdminService): StartupListener {
        return StartupListener(fallbackAdminService)
    }
}

fun main(params: Array<String>) {
    val props = parseProperties(loadProperties())

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

private fun parseProperties(props: Properties): ApplicationProperties {
    return object : ApplicationProperties {
        override val dbUrl: String
            get() = props["db.url"] as String
        override val dbUser: String
            get() = props["db.user"] as String
        override val dbPassword: String
            get() = props["db.password"] as String
        override val httpPort: Int
            get() = (props["http.port"] as String).toInt()
        override val serverSecret: String
            get() = props["server.secret"] as String
        override val serverNumber: Int
            get() = (props["server.number"] as String).toInt()
        override val adminMail: String
            get() = props["admin.mail"] as String
        override val adminPassword: String
            get() = props["admin.password"] as String
    }
}

private fun loadProperties(): Properties {
    val props = Properties()
    val fp = FileInputStream(File("src/main/resources/application.properties"))
    try {
        props.load(fp)
        return props
    } finally {
        fp.close()
    }
}
