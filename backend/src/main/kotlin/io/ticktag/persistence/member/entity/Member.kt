package io.ticktag.persistence.member.entity

import io.ticktag.persistence.project.entity.Project
import io.ticktag.persistence.user.entity.User
import java.io.Serializable
import java.time.Instant
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as MemberKey

        if (user.id != other.user.id) return false
        if (project.id != other.project.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.id.hashCode()
        result = 31 * result + project.id.hashCode()
        return result
    }
}

@Entity
@Table(name = "member")
@IdClass(MemberKey::class)
open class Member protected constructor() {
    companion object {
        fun create(user: User, project: Project, role: ProjectRole, joinDate: Instant): Member {
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

    @Column(name = "u_id", insertable = false, updatable = false)
    lateinit open var userId: UUID

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id", referencedColumnName = "id", nullable = false)
    lateinit open var project: Project
        protected set

    @Column(name = "p_id", insertable = false, updatable = false)
    lateinit open var projectId: UUID

    @Column(name = "project_role", nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit open var role: ProjectRole

    @Column(name = "join_date", nullable = false)
    lateinit open var joinDate: Instant

}