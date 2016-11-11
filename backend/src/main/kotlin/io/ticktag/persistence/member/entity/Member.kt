package io.ticktag.persistence.member.entity

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.user.entity.User
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Embeddable
data class MemberKey(val user: User, val project: Project) : Serializable

@Entity
@Table(name = "member")
@IdClass(MemberKey::class)
open class Member {
    companion object {
        fun create(user: User, project: Project, role: ProjectRole, joinDate: Date): Member {
            val m = Member()
            m.user = user
            m.project = project
            m.role = role
            m.joinDate = joinDate
            return m
        }
    }

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "u_id", referencedColumnName = "id")
    lateinit open var user: User
        protected set

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "p_id", referencedColumnName = "id")
    lateinit open var project: Project
        protected set

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit open var role: ProjectRole

    @Column(name = "join_date", nullable = false)
    lateinit open var joinDate: Date

    protected constructor()
}