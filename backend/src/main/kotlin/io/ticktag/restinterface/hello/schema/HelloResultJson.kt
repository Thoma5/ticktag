package io.ticktag.restinterface.hello.schema

import io.ticktag.service.hello.dto.HelloResult

data class HelloResultJson(
        val message: String
) {
    constructor(h: HelloResult) : this(h.message)
}
