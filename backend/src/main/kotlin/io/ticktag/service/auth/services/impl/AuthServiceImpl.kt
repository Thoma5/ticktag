package io.ticktag.service.auth.services.impl

import io.ticktag.TicktagService
import io.ticktag.service.auth.services.AuthService

@TicktagService
class AuthServiceImpl : AuthService {
    override fun isValidLogin(email: String, password: String): Boolean {
        // Quality security
        return true
    }
}