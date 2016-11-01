package io.ticktag.service.hello.impl

import io.ticktag.TicktagService
import io.ticktag.library.hello.HelloLibrary
import io.ticktag.service.hello.HelloService
import javax.inject.Inject

@TicktagService
class HelloServiceImpl @Inject constructor(private val hello: HelloLibrary) : HelloService {
    override fun hello(firstname: String, lastname: String): String {
        return hello.hello("$firstname $lastname")
    }
}
