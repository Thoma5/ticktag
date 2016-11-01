package io.ticktag.restinterface.hello.controllers.impl

import io.ticktag.TicktagRestInterface
import io.ticktag.restinterface.hello.controllers.HelloController
import io.ticktag.restinterface.hello.schema.HelloResultJson
import io.ticktag.service.hello.dto.HelloParams
import io.ticktag.service.hello.services.HelloService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@TicktagRestInterface
@RequestMapping("/hello")
class HelloControllerImpl(private val hello: HelloService) : HelloController {
    @RequestMapping(method = arrayOf(RequestMethod.GET))
    override fun hello(@RequestParam firstname: String, @RequestParam lastname: String): HelloResultJson {
        return HelloResultJson(hello.hello(HelloParams(firstname, lastname)))
    }
}
