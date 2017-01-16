package io.ticktag.persistence.member

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.MemberKey
import io.ticktag.persistence.member.entity.ProjectRole
import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.user.entity.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface MemberRepository : TicktagCrudRepository<Member, MemberKey> {
    fun findByUserAndUserDisabledIs(user: User, disabledUsers: Boolean): List<Member>?
    fun findByUserAndRoleNot(user: User, projectRole: ProjectRole): List<Member>?
    fun findByProjectAndUserDisabledIsAndRoleIs(project: Project, disabledUsers: Boolean, role: ProjectRole): List<Member>?
    fun findByProjectAndUserDisabledIsAndRoleNot(project: Project, disabledUsers: Boolean, role: ProjectRole): List<Member>?
    fun findByProject(project: Project): List<Member>?

    fun findByUserIdAndProjectId(userId: UUID, projectId: UUID): Member?
    @Query("SELECT m " +
            "FROM Member m join m.project p join p.tickets t " +
            "WHERE t.id = :ticketId and m.user.id = :userId")
    fun findByUserIdAndTicketId(@Param("userId") userId: UUID, @Param("ticketId") ticketId: UUID): Member?

    @Query("SELECT m " +
            "FROM Member m join m.project p " +
            "WHERE p.id = (Select t.project.id FROM Comment c join c.ticket t WHERE c.id = :commentId) and m.user.id = :userId")
    fun findByUserIdAndCommentId(@Param("userId") userId: UUID, @Param("commentId") commentId: UUID): Member?

    @Query("SELECT m " +
            "FROM Member m join m.project p " +
            "WHERE p.id = (Select t.project.id FROM LoggedTime l join l.comment c join c.ticket t WHERE l.id = :loggedTimeId) and m.user.id = :userId")
    fun findByUserIdAndLoggedTimeId(@Param("userId") userId: UUID, @Param("loggedTimeId") LoggedTimeId: UUID): Member?

    @Query("SELECT m " +
            "FROM Member m join m.project p join p.ticketTagGroups g " +
            "WHERE g.id = :ticketTagGroupId AND m.user.id = :userId")
    fun findByUserIdAndTicketTagGroupId(@Param("userId") id: UUID, @Param("ticketTagGroupId") ticketTagGroupId: UUID): Member?


    @Query("SELECT m " +
            "FROM Member m join m.project p join p.ticketTagGroups g join g.ticketTags t " +
            "WHERE t.id = :ticketTagId AND m.user.id = :userId")
    fun findByUserIdAndTicketTagId(@Param("userId") id: UUID, @Param("ticketTagId") ticketTagId: UUID): Member?
}
