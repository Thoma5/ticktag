package io.ticktag.persistence.member.entity

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.user.entity.User
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Embeddable
open class MemberKey protected constructor() : Serializable {
    companion object {
        fun create(user: User, project: Project): MemberKey {
            val mk = MemberKey()
            mk.user = user
            mk.project = project
            return mk
        }
    }

    lateinit open var user: User
        protected set

    lateinit open var project: Project
        protected set
}

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
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", referencedColumnName = "id", nullable = false)
    lateinit open var user: User
        protected set

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project
        protected set

    @Column(name = "projectRole", nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit open var role: ProjectRole

    @Column(name = "join_date", nullable = false)
    lateinit open var joinDate: Date

    protected constructor()
}