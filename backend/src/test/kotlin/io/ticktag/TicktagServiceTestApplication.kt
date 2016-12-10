package io.ticktag

import io.ticktag.restinterface.RestConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(TicktagBaseApplication::class)
open class TicktagServiceTestApplication {
    @Bean
    open fun timemachine(): TimeMachine {
        return TimeMachine()
    }
}

@Configuration
@Import(TicktagBaseApplication::class, RestConfig::class)
open class TicktagApiTestApplication {
    @Bean
    open fun timemachine(): TimeMachine {
        return TimeMachine()
    }
}
