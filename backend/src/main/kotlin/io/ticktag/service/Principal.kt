package io.ticktag.service

import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.loggedtime.LoggedTimeRepository
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.persistence.user.entity.Role
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

enum class Scope {
    INTERNAL,
    ANONYMOUS,
    REGULAR
}

data class Principal(
        val id: UUID,
        val role: Role?,
        val scope: Scope,
        private val members: MemberRepository?,
        private val comments: CommentRepository?,
        private val assignmenttags: AssignmentTagRepository?,
        private val timeCategories: TimeCategoryRepository?,
        private val loggedTimes: LoggedTimeRepository?
) {
    companion object {
        val INTERNAL = Principal(UUID(-1, -1), null, Scope.INTERNAL, null, null, null, null, null)
        val ANONYMOUS = Principal(UUID(-1, -1), null, Scope.ANONYMOUS, null, null, null, null, null)
    }

    fun isInternal(): Boolean = scope == Scope.INTERNAL

    fun isAnonymous(): Boolean = scope == Scope.ANONYMOUS

    fun isId(otherId: UUID?): Boolean {

        return this.id == otherId ?: return false
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

    fun userIdForCommentId(commentId: UUID): UUID? {
        if (comments == null) return null
        val comment = comments.findOne(commentId) ?: return null
        return comment.user.id

    }

    fun hasProjectRoleForLoggedTime(loggedTimeId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndLoggedTimeId(this.id, loggedTimeId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))

    }

    fun userIdForLoggedTimeId(loggId: UUID): UUID? {
        if (loggedTimes == null) return null
        val loggedTime = loggedTimes.findOne(loggId) ?: return null
        return loggedTime.comment.user.id

    }

    fun hasProjectRoleForAssignmentTag(assignmentTagId: UUID, roleString: String): Boolean {
        if (assignmenttags == null) return false
        val assignmenttag = assignmenttags.findOne(assignmentTagId) ?: return false
        return hasProjectRole(assignmenttag.project.id, roleString)
    }

    fun hasProjectRoleForTimeCategory(timeCategoryId: UUID, roleString: String): Boolean {
        timeCategories ?: return false
        val timeCategory = timeCategories.findOne(timeCategoryId) ?: return false
        return hasProjectRole(timeCategory.project.id, roleString)
    }

    fun hasProjectRoleForTicketAssignment(ticketId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndTicketId(this.id, ticketId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))
    }

    fun hasProjectRoleForTicketTagRelation(ticketId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndTicketId(this.id, ticketId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))
    }

    fun hasProjectRoleForTicketTagGroup(ticketTagGroupId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndTicketTagGroupId(this.id, ticketTagGroupId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))
    }

    fun hasProjectRoleForTicketTag(ticketTagId: UUID, roleString: String): Boolean {
        if (members == null) return false
        val member = members.findByUserIdAndTicketTagId(this.id, ticketTagId) ?: return false
        return member.role.includesRole(ProjectRole.valueOf(roleString))
    }

}
class AuthExpr private constructor() {
    companion object {
        const val ROLE_GLOBAL_ADMIN = "ADMIN"
        const val ROLE_GLOBAL_OBSERVER = "OBSERVER"
        const val ROLE_GLOBAL_USER = "USER"

        const val ROLE_PROJECT_ADMIN = "ADMIN"
        const val ROLE_PROJECT_OBSERVER = "OBSERVER"
        const val ROLE_PROJECT_USER = "USER"

        const val ANONYMOUS = "true"
        const val NOBODY = "false"
        const val INTERNAL = "principal.isInternal()"

        const val USER = "principal.hasRole('$ROLE_GLOBAL_USER')"
        const val OBSERVER = "principal.hasRole('$ROLE_GLOBAL_OBSERVER')"
        const val ADMIN = "principal.hasRole('$ROLE_GLOBAL_ADMIN')"

        const val PROJECT_OBSERVER = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRole(#authProjectId, '$ROLE_PROJECT_OBSERVER')"
        const val PROJECT_USER = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRole(#authProjectId, '$ROLE_PROJECT_USER')"
        const val PROJECT_ADMIN = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRole(#authProjectId, '$ROLE_PROJECT_ADMIN')"
        const val READ_TICKET = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForTicket(#authTicketId, '$ROLE_PROJECT_OBSERVER')"
        const val WRITE_TICKET = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTicket(#authTicketId, '$ROLE_PROJECT_USER')"

        const val READ_COMMENT = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForComment(#authCommentId, '$ROLE_PROJECT_OBSERVER')"
        const val CREATE_COMMENT = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTicket(#authTicketId, '$ROLE_PROJECT_USER') "
        const val EDIT_COMMENT = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForComment(#authCommentId, '$ROLE_PROJECT_ADMIN') || principal.isId(principal.userIdForCommentId(#authCommentId))"
        const val EDIT_TIME_LOG = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForLoggedTime(#authLoggedTimeId, '$ROLE_PROJECT_ADMIN') || principal.isId(principal.userIdForLoggedTimeId(#authLoggedTimeId))"
        const val READ_TIME_LOG = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForLoggedTime(#authLoggedTimeId, '$ROLE_PROJECT_OBSERVER') || principal.isId(principal.userIdForLoggedTimeId(#authLoggedTimeId))"


        const val READ_TICKET_TAG_GROUP = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, '$ROLE_PROJECT_OBSERVER')"
        const val CREATE_TICKET_TAG_GROUP = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRole(#authProjectId, '$ROLE_PROJECT_ADMIN')"
        const val EDIT_TICKET_TAG_GROUP = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, '$ROLE_PROJECT_ADMIN')"

        const val READ_TICKET_TAG = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForTicketTag(#authTicketTagId, '$ROLE_PROJECT_OBSERVER')"
        const val READ_TICKET_TAG_FOR_GROUP = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, '$ROLE_PROJECT_OBSERVER')"
        const val CREATE_TICKET_TAG = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, '$ROLE_PROJECT_ADMIN')"
        const val EDIT_TICKET_TAG = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTicketTag(#authTicketTagId, '$ROLE_PROJECT_ADMIN')"

        const val READ_ASSIGNMENTTAG = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForAssignmentTag(#authAssignmentTagId, '$ROLE_PROJECT_OBSERVER')"
        const val EDIT_ASSIGNMENTTAG = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForAssignmentTag(#authAssignmentTagId, '$ROLE_PROJECT_USER')"

        const val READ_TIMECATEGORY = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForTimeCategory(#authTimeCategoryId, '$ROLE_PROJECT_OBSERVER')"
        const val WRITE_TIMECATEGORY = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTimeCategory(#authTimeCategoryId, '$ROLE_PROJECT_ADMIN')"

        const val READ_TICKET_ASSIGNMENT = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForTicketAssignment(#authTicketId, '$ROLE_PROJECT_OBSERVER')"
        const val WRITE_TICKET_ASSIGNMENT = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTicketAssignment(#authTicketId, '$ROLE_PROJECT_USER')"

        const val READ_TICKET_TAG_RELATION = "principal.hasRole('$ROLE_GLOBAL_OBSERVER') || principal.hasProjectRoleForTicketTagRelation(#authTicketId, '$ROLE_PROJECT_OBSERVER')"
        const val WRITE_TICKET_TAG_RELATION = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.hasProjectRoleForTicketTagRelation(#authTicketId, '$ROLE_PROJECT_USER')"

        const val ADMIN_OR_SELF = "principal.hasRole('$ROLE_GLOBAL_ADMIN') || principal.isId(#userId)"

    }
}

fun <T> withInternalPrincipal(cb: () -> T): T {
    val ctx = SecurityContextHolder.getContext()
    if (ctx.authentication != null)
        throw Exception("Cannot replace the current security principal.")
    ctx.authentication = PreAuthenticatedAuthenticationToken(Principal.INTERNAL, null, emptySet())
    try {
        return cb()
    } finally {
        ctx.authentication = null
    }
}
