package io.ticktag.persistence

import io.ticktag.ApplicationProperties
import org.apache.commons.dbcp.BasicDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.sql.Connection
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@ComponentScan(basePackages = arrayOf("io.ticktag.persistence"))
@EnableJpaRepositories("io.ticktag.persistence")
open class PersistenceConfig {
    @Bean
    open fun dataSource(props: ApplicationProperties): DataSource {
        val basicDataSource = BasicDataSource()
        basicDataSource.driverClassName = "org.postgresql.Driver"
        basicDataSource.url = props.dbUrl
        basicDataSource.username = props.dbUser
        basicDataSource.password = props.dbPassword
        basicDataSource.defaultTransactionIsolation = Connection.TRANSACTION_SERIALIZABLE
        return basicDataSource
    }

    @Bean(name = arrayOf("entityManagerFactory"))
    open fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.dataSource = dataSource
        entityManagerFactoryBean.jpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.setPackagesToScan("io.ticktag.persistence.user.entity")

        val jpaProperties = Properties()
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
        jpaProperties.put("hibernate.hbm2ddl.auto", "validate")
        jpaProperties.put("hibernate.show_sql", "true")
        jpaProperties.put("hibernate.globally_quoted_identifiers", "true")
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