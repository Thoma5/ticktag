package io.ticktag.service

import io.ticktag.persistence.LoggedTime.LoggedTimeRepository
import io.ticktag.persistence.comment.CommentRepository
import io.ticktag.persistence.member.MemberRepository
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.ticket.AssignmentTagRepository
import io.ticktag.persistence.timecategory.TimeCategoryRepository
import io.ticktag.persistence.user.entity.Role
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.*

data class Principal(
        val id: UUID,
        val role: Role?,
        private val members: MemberRepository?,
        private val comments: CommentRepository?,
        private val assignmenttags: AssignmentTagRepository?,
        private val timeCategories: TimeCategoryRepository?,
        private val loggedTimes: LoggedTimeRepository?
) {
    companion object {
        val INTERNAL = Principal(UUID(-1, -1), null, null, null, null, null, null)
    }

    fun isInternal(): Boolean = members == null

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
        val assignmenttag = assignmenttags.findOne(assignmentTagId)
        if (assignmenttag == null) return false
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
        const val ANONYMOUS = "true"
        const val NOBODY = "false"
        const val INTERNAL = "principal.isInternal()"

        const val USER = "principal.hasRole('USER')"
        const val OBSERVER = "principal.hasRole('OBSERVER')"
        const val ADMIN = "principal.hasRole('ADMIN')"

        const val PROJECT_OBSERVER = "principal.hasRole('OBSERVER') || principal.hasProjectRole(#authProjectId, 'OBSERVER')"
        const val PROJECT_USER = "principal.hasRole('ADMIN') || principal.hasProjectRole(#authProjectId, 'USER')"
        const val PROJECT_ADMIN = "principal.hasRole('ADMIN') || principal.hasProjectRole(#authProjectId, 'ADMIN')"
        const val READ_TICKET = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTicket(#authTicketId, 'OBSERVER')"
        const val WRITE_TICKET = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicket(#authTicketId, 'USER')"

        const val READ_COMMENT = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForComment(#authCommentId, 'OBSERVER')"
        const val CREATE_COMMENT = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicket(#authTicketId, 'USER') "
        const val EDIT_COMMENT = "principal.hasRole('ADMIN') || principal.hasProjectRoleForComment(#authCommentId, 'ADMIN') || principal.isId(principal.userIdForCommentId(#authCommentId))"
        const val EDIT_TIME_LOG = "principal.hasRole('ADMIN') || principal.hasProjectRoleForLoggedTime(#authLoggedTimeId, 'ADMIN') || principal.isId(principal.userIdForLoggedTimeId(#authLoggedTimeId))"
        const val READ_TIME_LOG = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForLoggedTime(#authLoggedTimeId, 'OBSERVER') || principal.isId(principal.userIdForLoggedTimeId(#authLoggedTimeId))"


        const val READ_TICKET_TAG_GROUP = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, 'OBSERVER')"
        const val CREATE_TICKET_TAG_GROUP = "principal.hasRole('ADMIN') || principal.hasProjectRole(#authProjectId, 'ADMIN')"
        const val EDIT_TICKET_TAG_GROUP = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, 'ADMIN')"

        const val READ_TICKET_TAG = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTicketTag(#authTicketTagId, 'OBSERVER')"
        const val READ_TICKET_TAG_FOR_GROUP = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, 'OBSERVER')"
        const val CREATE_TICKET_TAG = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicketTagGroup(#authTicketTagGroupId, 'ADMIN')"
        const val EDIT_TICKET_TAG = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicketTag(#authTicketTagId, 'ADMIN')"

        const val READ_ASSIGNMENTTAG = "principal.hasRole('ADMIN') || principal.hasProjectRoleForAssignmentTag(#authAssignmentTagId, 'OBSERVER')"
        const val EDIT_ASSIGNMENTTAG = "principal.hasRole('ADMIN') || principal.hasProjectRoleForAssignmentTag(#authAssignmentTagId, 'USER')"

        const val READ_TIMECATEGORY = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTimeCategory(#authTimeCategoryId, 'OBSERVER')"
        const val WRITE_TIMECATEGORY = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTimeCategory(#authTimeCategoryId, 'ADMIN')"

        const val READ_TICKET_ASSIGNMENT = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTicketAssignment(#authTicketId, 'OBSERVER')"
        const val WRITE_TICKET_ASSIGNMENT = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicketAssignment(#authTicketId, 'USER')"

        const val READ_TICKET_TAG_RELATION = "principal.hasRole('OBSERVER') || principal.hasProjectRoleForTicketTagRelation(#authTicketId, 'OBSERVER')"
        const val WRITE_TICKET_TAG_RELATION = "principal.hasRole('ADMIN') || principal.hasProjectRoleForTicketTagRelation(#authTicketId, 'USER')"

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
