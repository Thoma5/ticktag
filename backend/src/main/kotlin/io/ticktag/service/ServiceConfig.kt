package io.ticktag.service

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = arrayOf("io.ticktag.service"))
open class ServiceConfig {
}