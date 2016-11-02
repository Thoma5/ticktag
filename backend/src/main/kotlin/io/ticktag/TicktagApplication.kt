package io.ticktag

import io.ticktag.library.LibraryConfig
import io.ticktag.persistence.PersistenceConfig
import io.ticktag.restinterface.RestConfig
import io.ticktag.restinterface.SwaggerConfig
import io.ticktag.service.ServiceConfig
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.filter.DelegatingFilterProxy
import org.springframework.web.servlet.DispatcherServlet
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.servlet.DispatcherType

@Configuration
@Import(PersistenceConfig::class, LibraryConfig::class, ServiceConfig::class, RestConfig::class, SwaggerConfig::class)
@EnableAspectJAutoProxy
open class TicktagApplication {
    @Bean("applicationProperties")
    open fun applicationProperties(): Properties = loadProperties()
}

fun main(params: Array<String>) {
    val props = loadProperties()

    val context = AnnotationConfigWebApplicationContext()
    context.register(TicktagApplication::class.java)

    val contextHandler = ServletContextHandler()
    contextHandler.errorHandler = null
    contextHandler.contextPath = "/"
    contextHandler.addServlet(ServletHolder(DispatcherServlet(context)), "/")
    contextHandler.addEventListener(ContextLoaderListener(context))
    contextHandler.addFilter(FilterHolder(DelegatingFilterProxy("springSecurityFilterChain")), "/*", EnumSet.allOf(DispatcherType::class.java))

    val server = Server(Integer.valueOf(props.getProperty("http.port")))
    server.handler = contextHandler

    server.start()
    server.join()
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
