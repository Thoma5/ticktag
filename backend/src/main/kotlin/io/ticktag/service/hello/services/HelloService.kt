package io.ticktag.service.hello.services

import io.ticktag.service.hello.dto.HelloParams
import io.ticktag.service.hello.dto.HelloResult

interface HelloService {
    fun hello(params: HelloParams): HelloResult
}