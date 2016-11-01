package io.ticktag

import org.apache.catalina.startup.Tomcat
import org.apache.commons.dbcp.BasicDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.inject.Named
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@ComponentScan
@EnableWebMvc
@EnableJpaRepositories("io.ticktag.persistence")
@EnableSwagger2
open class TicktagApplication : WebMvcConfigurerAdapter() {
    @Bean("applicationProperties")
    open fun applicationProperties(): Properties {
        val props = Properties()
        val fp = FileInputStream(File("src/main/resources/application.properties"))
        props.load(fp)
        fp.close()
        return props
    }

    @Bean
    open fun dataSource(@Named("applicationProperties") props: Properties): DataSource {
        val basicDataSource = BasicDataSource()
        basicDataSource.driverClassName = "org.postgresql.Driver"
        basicDataSource.url = props.getProperty("db.url")
        basicDataSource.username = props.getProperty("db.user")
        basicDataSource.password = props.getProperty("db.password")
        return basicDataSource
    }

    @Bean(name = arrayOf("entityManagerFactory"))
    open fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.dataSource = dataSource
        entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.setPackagesToScan("io.ticktag.persistence.entity")

        val jpaProperties = Properties()
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
        jpaProperties.put("hibernate.hbm2ddl.auto", "update")
        jpaProperties.put("hibernate.show_sql", "true")
        jpaProperties.put("hibernate.format_sql", "true")
        entityManagerFactoryBean.setJpaProperties(jpaProperties)

        return entityManagerFactoryBean
    }


    @Bean
    open fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = emf
        return transactionManager
    }

    @Bean
    open fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build()
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/")
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
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
