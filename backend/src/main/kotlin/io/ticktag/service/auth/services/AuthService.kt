package io.ticktag.service.auth.services

interface AuthService {
    fun isValidLogin(email: String, password: String): Boolean
}