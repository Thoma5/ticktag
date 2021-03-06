package io.ticktag.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.io.Serializable
import javax.persistence.EntityManager

@NoRepositoryBean
interface TicktagCrudRepository<E, ID: Serializable> : Repository<E, ID>, TicktagBaseRepositoryInsert<E> {
    fun findOne(id: ID): E?
    fun exists(id: ID): Boolean
    fun count(): Int
    fun findAll(): List<E>
    fun findAll(pageable: Pageable): Page<E>

    fun delete(entity: E)
}

interface TicktagBaseRepositoryInsert<in E> {
    fun insert(entity: E)
}

open class TicktagBaseRepositoryImpl<E, ID: Serializable>(
        ei: JpaEntityInformation<E, ID>,
        private val em: EntityManager
) : SimpleJpaRepository<E, ID>(ei, em), TicktagBaseRepositoryInsert<E> {

    override fun insert(entity: E) {
        em.persist(entity)
    }
}
