package io.ticktag.service.hello

interface HelloService {
    fun hello(firstname: String, lastname: String): String
}