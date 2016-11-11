package io.ticktag.persistence.member.entity

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "member")
@IdClass(MemberKey::class)
open class Member {

    fun create(id: MemberKey, role: ProjectRole, joinDate: Date): Member {
        val m = Member()
        m.id = id
        m.role = role
        m.joinDate = joinDate
        return m
    }

    @EmbeddedId
    lateinit open var id: MemberKey

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit open var role: ProjectRole


    @Column(name = "join_date", nullable = false)
    lateinit open var joinDate: Date

    protected constructor()
}