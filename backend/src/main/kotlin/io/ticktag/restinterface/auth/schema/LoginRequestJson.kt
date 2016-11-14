package io.ticktag.restinterface.auth.schema

data class LoginRequestJson(
        val email: String,
        val password: String
)
