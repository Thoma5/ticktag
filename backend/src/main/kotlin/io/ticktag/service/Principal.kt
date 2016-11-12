package io.ticktag.service

import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.user.entity.Role
import io.ticktag.persistence.user.entity.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

data class Principal(
        val id: UUID, //TODO: Maybe we dont need this any more
        val authorities: Set<String>,
        val user: User
) {
    companion object {
        //TODO: Maybe this user is wrong
        val INTERNAL = Principal(UUID(-1, -1), setOf("INTERNAL"), User.createWithId(UUID.randomUUID(), "","","",Role.USER, UUID.randomUUID()))
    }

    fun isMember(projectId: UUID) : Boolean {
        return user.memberships.any { m -> m.project.id == projectId }
    }

    fun isMemberAndHasProjectRole(projectId: UUID, roleString: String) : Boolean {
        val role = ProjectRole.valueOf(roleString)
        return user.memberships.any { m -> m.project.id == projectId && m.role.includesRole(role) }
    }
}

class AuthExpr private constructor() {
    companion object {
        const val ANONYMOUS = "true"
        const val NOBODY = "false"

        const val USER = "hasAuthority('USER')"
        const val ADMIN = "hasAuthority('ADMIN')"
        const val OBSERVER = "hasAuthority('OBSERVER')"
        const val INTERNAL = "hasAuthority('INTERNAL')"

        const val PROJECT_OBSERVER = "hasAuthority('OBSERVER') || principal.isMemberAndHasProjectRole(#authProjectId, 'OBSERVER')"
        const val PROJECT_USER = "hasAuthority('ADMIN') || principal.isMemberAndHasProjectRole(#authProjectId, 'USER')"
        const val PROJECT_ADMIN = "hasAuthority('ADMIN') || principal.isMemberAndHasProjectRole(#authProjectId, 'ADMIN')"
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
