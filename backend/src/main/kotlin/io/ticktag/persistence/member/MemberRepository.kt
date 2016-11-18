package io.ticktag.persistence.member

import io.ticktag.TicktagRepository
import io.ticktag.persistence.TicktagCrudRepository
import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.member.entity.MemberKey
import io.ticktag.persistence.project.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

@TicktagRepository
interface MemberRepository : TicktagCrudRepository<Member, MemberKey> {
    fun findByUser(user: UUID, pageable: Pageable): Page<Project>
    fun findByProject(project: UUID, pageable: Pageable): Page<Project>

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
            "FROM Member m join m.project p join p.ticketTagGroups g " +
            "WHERE g.id = :ticketTagGroupId AND m.user.id = :userId")
    fun findByUserIdAndTicketTagGroupId(@Param("userId") id: UUID, @Param("ticketTagGroupId")ticketTagGroupId: UUID): Member?
}
