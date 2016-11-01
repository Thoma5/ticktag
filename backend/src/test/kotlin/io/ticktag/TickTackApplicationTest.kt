package io.ticktag

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.springframework.context.annotation.AnnotationConfigApplicationContext

@RunWith(JUnit4::class)
class TickTackApplicationTest {
    @Test
    fun context_isInitializedSuccessfully() {
        val context = AnnotationConfigApplicationContext(TicktagApplication::class.java)
    }
}
