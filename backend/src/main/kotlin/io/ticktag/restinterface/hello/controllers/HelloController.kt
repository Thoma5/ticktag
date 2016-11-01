package io.ticktag.restinterface.hello.controllers

import io.ticktag.restinterface.hello.schema.HelloResultJson
import io.ticktag.service.hello.dto.HelloResult

interface HelloController {
    fun hello(firstname: String, lastname: String): HelloResultJson
}
