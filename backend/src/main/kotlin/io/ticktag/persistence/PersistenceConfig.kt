package io.ticktag.persistence

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.*
import javax.inject.Named
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@ComponentScan(basePackages = arrayOf("io.ticktag.persistence"))
@EnableJpaRepositories("io.ticktag.persistence")
open class PersistenceConfig {
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