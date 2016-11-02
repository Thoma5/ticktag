package io.ticktag.persistence

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import java.util.*

@NoRepositoryBean
interface TicktagCrudRepository<E>: Repository<E, UUID> {
    fun <S: E> save(entity: S): S

    fun findById(id: UUID): E?
    fun exists(id: UUID): Boolean

    fun delete(entity: E)
}
