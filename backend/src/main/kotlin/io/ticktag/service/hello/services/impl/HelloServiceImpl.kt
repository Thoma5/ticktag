package io.ticktag.service.hello.services.impl

import io.ticktag.TicktagService
import io.ticktag.library.hello.HelloLibrary
import io.ticktag.persistence.PersonRepository
import io.ticktag.persistence.entity.Person
import io.ticktag.service.hello.dto.HelloParams
import io.ticktag.service.hello.dto.HelloResult
import io.ticktag.service.hello.services.HelloService
import javax.inject.Inject

@TicktagService
class HelloServiceImpl @Inject constructor(
        private val hello: HelloLibrary,
        private val personRepository: PersonRepository
) : HelloService {

    override fun hello(params: HelloParams): HelloResult {
        val person = Person(params.firstName, params.lastName)
        personRepository.save(person)

        val name = "${params.firstName} ${params.lastName}"
        return HelloResult(hello.hello(name))
    }
}
