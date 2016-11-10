package io.ticktag.persistence

import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository
import java.io.Serializable
import java.util.*
import javax.persistence.EntityManager

@NoRepositoryBean
interface TicktagCrudRepository<E> : PagingAndSortingRepository<E, UUID>, TicktagBaseRepositoryInsert<E> {
    fun findById(id: UUID): E?
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
