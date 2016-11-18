package io.ticktag.service

import io.ticktag.BaseTest
import io.ticktag.TicktagServiceTestApplication
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = arrayOf(TicktagServiceTestApplication::class))
abstract class ServiceBaseTest : BaseTest()