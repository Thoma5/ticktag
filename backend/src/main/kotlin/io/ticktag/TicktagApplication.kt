package io.ticktag

import io.ticktag.service.hello.dto.HelloParams
import io.ticktag.service.hello.services.impl.HelloServiceImpl
import org.apache.commons.dbcp.BasicDataSource
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@ComponentScan
@EnableJpaRepositories("io.ticktag.persistence")
open class TicktagApplication {
    private val DB_URL = "jdbc:postgresql://%s:%d%s"

    @Bean
    open fun dataSource(): DataSource {
        val username = "ticktag"
        val password = "ticktag"
        val host = "localhost"
        val port = 5432
        val path = "/ticktag"
        val dbUrl = String.format(DB_URL, host, port, path)

        val basicDataSource = BasicDataSource()
        basicDataSource.driverClassName = "org.postgresql.Driver"
        basicDataSource.url = dbUrl
        basicDataSource.username = username
        basicDataSource.password = password

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
}

fun main(params: Array<String>) {
    val context = AnnotationConfigApplicationContext(TicktagApplication::class.java)
    val hello = context.getBean(HelloServiceImpl::class.java)
    println(hello.hello(HelloParams("Tick", "Tag")))
}
