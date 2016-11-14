package io.ticktag

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(TicktagBaseApplication::class)
open class TicktagTestApplication
