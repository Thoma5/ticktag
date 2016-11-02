package io.ticktag.restinterface.hello.controllers

import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.hello.schema.HelloResultJson
import io.ticktag.service.hello.dto.HelloParams
import io.ticktag.service.hello.services.HelloService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import javax.inject.Inject

@TicktagRestInterface
@RequestMapping("/hello")
open class HelloControllerImpl @Inject constructor(
        private val hello: HelloService
) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    open fun hello(@RequestParam firstname: String, @RequestParam lastname: String): HelloResultJson {
        return HelloResultJson(hello.hello(HelloParams(firstname, lastname)))
    }
}
