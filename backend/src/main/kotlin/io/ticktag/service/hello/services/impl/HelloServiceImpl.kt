package io.ticktag.service.hello.services.impl

import io.ticktag.TicktagService
import io.ticktag.library.hello.HelloLibrary
import io.ticktag.service.hello.dto.HelloParams
import io.ticktag.service.hello.dto.HelloResult
import io.ticktag.service.hello.services.HelloService
import javax.inject.Inject

@TicktagService
class HelloServiceImpl @Inject constructor(
        private val hello: HelloLibrary
) : HelloService {

    override fun hello(params: HelloParams): HelloResult {
        val name = "${params.firstName} ${params.lastName}"
        return HelloResult(hello.hello(name))
    }
}
