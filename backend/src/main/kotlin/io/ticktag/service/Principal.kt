package io.ticktag.service

import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.user.entity.Role
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

data class Principal(
        val id: UUID,
        val role: Role?,
        private val members: MemberRepository?,
        private val comments: CommentRepository?
) {
    companion object {
        val INTERNAL = Principal(UUID(-1, -1), null, null, null)
    }

    fun isInternal(): Boolean = members == null

    fun isId(otherId: UUID): Boolean {
        return this.id == otherId
    }

    fun hasRole(roleString: String): Boolean {
        if (role == null) return false
        else return role.includesRole(Role.valueOf(roleString))
    }

    fun hasProjectRole(projectId: UUID, roleString: String): Boolean {
        if (members == null) return false

        val member = members.findByUserIdAndProjectId(id, projectId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))
    }

    fun hasProjectRoleForTicket(ticketId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndTicketId(this.id, ticketId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))

    }

    fun hasProjectRoleForComment(commentId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndCommentId(this.id, commentId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))

    }

    fun userIdForCommentId(commentId: UUID): UUID {
        if (comments == null) return UUID.fromString("invalid")
        val comment = comments.findOne(commentId) ?: return UUID.fromString("invalid")
        return comment.user.id

    }


    fun hasProjectRoleForTicketTagGroup(ticketTagGroupId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndTicketTagGroupId(this.id, ticketTagGroupId) ?: return false
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

        const val READ_COMMENT = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForComment(#authCommentId, 'OBSERVER')"
        const val CREATE_COMMENT = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicket(#authTicketId, 'USER') "
        const val EDIT_COMMENT = "principal.hasRole('ADMIN') || principal.hasProjectRoleForComment(#authCommentId, 'ADMIN') || principal.isId(principal.userIdForCommentId(#authCommentId))"

        const val READ_TICKET_TAG_GROUP = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, 'OBSERVER')"
        const val CREATE_TICKET_TAG_GROUP = "principal.hasRole('ADMIN') || principal.hasProjectRole(#authProjectId, 'ADMIN')"
        const val EDIT_TICKET_TAG_GROUP = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, 'ADMIN')"

        const val ADMIN_OR_SELF = "principal.hasRole('ADMIN') || principal.isId(#userId)"


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
