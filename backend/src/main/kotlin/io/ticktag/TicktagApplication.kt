package io.ticktag

import io.ticktag.service.hello.services.HelloService
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
open class TicktagApplication {

}

fun main(params: Array<String>) {
    val context = AnnotationConfigApplicationContext(TicktagApplication::class.java)
    val hello = context.getBean(HelloService::class.java)
    println(hello.hello("Tick", "Tag"))
}
