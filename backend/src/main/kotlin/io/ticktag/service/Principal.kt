package io.ticktag.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

data class Principal(
        val id: UUID,
        val authorities: Set<String>
) {
    companion object {
        val INTERNAL = Principal(UUID(-1, -1), setOf("INTERNAL"))
    }
}

class AuthExpr private constructor() {
    companion object {
        const val USER = "hasAuthority('USER')"
        const val ANONYMOUS = "true"
        const val NOBODY = "false"
        const val ADMIN = "hasAuthority('ADMIN')"
        const val INTERNAL = "hasAuthority('INTERNAL')"
    }
}

fun <T> withInternalPrincipal(cb: () -> T): T {
    val ctx = SecurityContextHolder.getContext()
    if (ctx.authentication != null)
        throw Exception("Cannot replate the current security principal.")
    ctx.authentication = PreAuthenticatedAuthenticationToken(Principal.INTERNAL, null, Principal.INTERNAL.authorities.map(::SimpleGrantedAuthority))
    try {
        return cb()
    } finally {
        ctx.authentication = null
    }
}
