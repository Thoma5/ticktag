package io.ticktag.service.hello.services

import io.ticktag.TicktagService
import io.ticktag.library.hello.HelloLibrary
import org.springframework.beans.factory.annotation.Autowired

@TicktagService
open class HelloService @Autowired constructor(private val hello: HelloLibrary) {
    open fun hello(firstname: String, lastname: String): String {
        return hello.hello("$firstname $lastname")
    }
}
