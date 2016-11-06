package io.ticktag

import io.ticktag.service.fallbackadmin.services.FallbackAdminService
import io.ticktag.service.withInternalPrincipal
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
open class StartupListener @Inject constructor(
        private val fallbackAdminService: FallbackAdminService
) : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        withInternalPrincipal {
            fallbackAdminService.ensureAdminExists()
        }
    }
}