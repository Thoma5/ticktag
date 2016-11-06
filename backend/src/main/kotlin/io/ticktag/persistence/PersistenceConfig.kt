package io.ticktag.persistence

import io.ticktag.ApplicationProperties
import org.apache.commons.dbcp.BasicDataSource
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.dao.CannotAcquireLockException
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.sql.Connection
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@ComponentScan(basePackages = arrayOf("io.ticktag.persistence"))
@EnableJpaRepositories("io.ticktag.persistence", repositoryBaseClass = TicktagBaseRepositoryImpl::class, enableDefaultTransactions = false)
@EnableTransactionManagement(order = 300)
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

    @Bean
    open fun transactionRepeatAspect() = TransactionRepeatAspect()
}

@Order(200)
@Aspect
class TransactionRepeatAspect {
    companion object {
        private val LOG = LoggerFactory.getLogger(TransactionRepeatAspect::class.java)
    }

    private val nested = ThreadLocal.withInitial { false }

    @Around("@within(io.ticktag.TicktagService)")
    fun around(pjp: ProceedingJoinPoint): Any? {
        if (nested.get()) {
            return pjp.proceed()
        } else {
            nested.set(true)
            try {
                var tries = 3
                while (true) {
                    try {
                        return pjp.proceed()
                    } catch (ex: CannotAcquireLockException) {
                        if (tries == 0) {
                            throw ex
                        } else {
                            LOG.warn("Serialization problem in database transaction. Retry.")
                            --tries
                        }
                    }
                }
            } finally {
                nested.set(false)
            }
        }
    }
}
