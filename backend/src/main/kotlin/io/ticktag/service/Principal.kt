package io.ticktag.service

import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.user.entity.Role
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

data class Principal(
        val id: UUID,
        val role: Role?,
        private val members: MemberRepository?
) {
    companion object {
        val INTERNAL = Principal(UUID(-1, -1), null, null)
    }

    fun isInternal(): Boolean = members == null

    fun hasRole(roleString: String): Boolean {
        if (role == null) return false
        else return role.includesRole(Role.valueOf(roleString))
    }

    fun hasProjectRole(projectId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndProjectId(id, projectId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))
    }
}

class AuthExpr private constructor() {
    companion object {
        const val ANONYMOUS = "true"
        const val NOBODY = "false"
        const val INTERNAL = "principal.isInternal()"

        const val USER = "principal.hasRole('USER')"
        const val OBSERVER = "principal.hasRole('OBSERVER')"
        const val ADMIN = "principal.hasRole('ADMIN')"

        const val PROJECT_OBSERVER = "principal.hasRole('OBSERVER') || principal.hasProjectRole(#authProjectId, 'OBSERVER')"
        const val PROJECT_USER = "principal.hasRole('ADMIN') || principal.hasProjectRole(#authProjectId, 'USER')"
        const val PROJECT_ADMIN = "principal.hasRole('ADMIN') || principal.hasProjectRole(#authProjectId, 'ADMIN')"
    }
}

fun <T> withInternalPrincipal(cb: () -> T): T {
    val ctx = SecurityContextHolder.getContext()
    if (ctx.authentication != null)
        throw Exception("Cannot replate the current security principal.")
    ctx.authentication = PreAuthenticatedAuthenticationToken(Principal.INTERNAL, null, emptySet())
    try {
        return cb()
    } finally {
        ctx.authentication = null
    }
}
