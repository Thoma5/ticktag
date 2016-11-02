package io.ticktag

import io.ticktag.library.LibraryConfig
import io.ticktag.persistence.PersistenceConfig
import io.ticktag.restinterface.RestConfig
import io.ticktag.service.ServiceConfig
import org.apache.catalina.startup.Tomcat
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import java.io.File
import java.io.FileInputStream
import java.util.*

@Configuration
@Import(PersistenceConfig::class, LibraryConfig::class, ServiceConfig::class, RestConfig::class)
open class TicktagApplication {
    @Bean("applicationProperties")
    open fun applicationProperties(): Properties {
        val props = Properties()
        val fp = FileInputStream(File("src/main/resources/application.properties"))
        props.load(fp)
        fp.close()
        return props
    }
}

fun main(params: Array<String>) {
    val pwd = File("")

    val context = AnnotationConfigWebApplicationContext()
    context.register(TicktagApplication::class.java)

    val props = Properties()
    val fp = FileInputStream(File("src/main/resources/application.properties"))
    props.load(fp)
    fp.close()

    val tomcat = Tomcat()
    tomcat.setPort(Integer.valueOf(props.getProperty("http.port")))
    tomcat.addContext("", pwd.absolutePath)
    val tcServlet = tomcat.addServlet("", "TicktagDispatcherServlet", DispatcherServlet(context))
    tcServlet.addMapping("/*")
    tcServlet.loadOnStartup = 1
    tomcat.start()
    tomcat.server.await()
}
