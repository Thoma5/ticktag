package io.ticktag.persistence

import org.springframework.data.repository.Repository
import java.util.*

interface TicktagCrudRepository<E>: Repository<E, UUID> {
    fun <S: E> create(entity: S): S

    fun findById(id: UUID): E?
    fun exists(id: UUID): Boolean

    fun delete(entity: E)
}
