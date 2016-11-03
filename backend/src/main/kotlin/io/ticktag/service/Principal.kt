package io.ticktag.service

import java.util.*

data class Principal(
        val id: UUID,
        val authorities: Set<String>
) {}

class AuthExpr private constructor() {
    companion object {
        const val USER = "hasAuthority('USER')"
        const val ANONYMOUS = "true"
        const val NOBODY = "false"
        const val ADMIN = "hasAuthority('ADMIN')"
    }
}
