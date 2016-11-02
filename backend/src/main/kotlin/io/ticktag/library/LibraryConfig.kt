package io.ticktag.library

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = arrayOf("io.ticktag.library"))
open class LibraryConfig {
}