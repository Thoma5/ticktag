package io.ticktag

import org.junit.Test
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

class TickTackApplicationTest {
    @Test
    fun context_isInitializedSuccessfully() {
        val context = AnnotationConfigWebApplicationContext()
        context.register(TicktagApplication::class.java)
    }
}
