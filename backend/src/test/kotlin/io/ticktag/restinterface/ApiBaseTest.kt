package io.ticktag.restinterface

import io.ticktag.BaseTest
import io.ticktag.TicktagApiTestApplication
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration

@ContextConfiguration(classes = arrayOf(TicktagApiTestApplication::class))
@WebAppConfiguration
abstract class ApiBaseTest : BaseTest()