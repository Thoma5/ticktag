package io.ticktag

import io.ticktag.service.hello.impl.HelloServiceImpl
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
open class TicktagApplication {

}

fun main(params: Array<String>) {
    val context = AnnotationConfigApplicationContext(TicktagApplication::class.java)
    val hello = context.getBean(HelloServiceImpl::class.java)
    println(hello.hello("Tick", "Tag"))
}
