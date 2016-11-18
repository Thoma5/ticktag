package io.ticktag

import io.ticktag.restinterface.RestConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.web.WebAppConfiguration

@Configuration
@Import(TicktagBaseApplication::class)
open class TicktagServiceTestApplication

@Configuration
@Import(TicktagBaseApplication::class, RestConfig::class)
open class TicktagApiTestApplication
