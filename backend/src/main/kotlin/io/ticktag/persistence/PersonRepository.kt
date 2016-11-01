package io.ticktag.persistence

import io.ticktag.TicktagRepository
import io.ticktag.persistence.entity.Person
import org.springframework.data.repository.CrudRepository
import java.util.*

@TicktagRepository
interface PersonRepository : CrudRepository<Person, UUID> {
    fun findById(id: UUID): Person?
}