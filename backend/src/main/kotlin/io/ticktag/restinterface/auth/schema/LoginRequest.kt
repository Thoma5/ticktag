package io.ticktag.restinterface.auth.schema

data class LoginRequest(
        val email: String,
        val password: String
) {}
