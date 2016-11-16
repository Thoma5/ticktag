package io.ticktag.persistence.user.entity

import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.ticket.entity.*
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
open class User protected constructor() {
    companion object {
        fun create(mail: String, passwordHash: String, name: String, role: Role, currentToken: UUID, profilePic: ByteArray?): User {
            return createWithId(UUID.randomUUID(), mail, passwordHash, name, role, currentToken, profilePic)
        }

        fun createWithId(uuid: UUID, mail: String, passwordHash: String, name: String, role: Role,
                         currentToken: UUID, profilePic: ByteArray?): User {
            val u = User()
            u.id = uuid
            u.mail = mail
            u.passwordHash = passwordHash
            u.name = name
            u.role = role
            u.currentToken = currentToken
            u.profilePic = profilePic
            u.memberships = mutableListOf()
            u.createdTickets = mutableListOf()
            u.comments = mutableListOf()
            u.assignedTicketUsers = mutableListOf()
            u.events = mutableListOf()
            u.userAddedEvents = mutableListOf()
            u.userRemovedEvents = mutableListOf()
            return u
        }
    }

    @Id
    @Column(name = "id")
    lateinit open var id: UUID
        protected set

    @Column(name = "mail", nullable = false)
    lateinit open var mail: String

    @Column(name = "password_hash", nullable = false)
    lateinit open var passwordHash: String

    @Column(name = "name", nullable = false)
    lateinit open var name: String

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit open var role: Role

    @Column(name = "current_token", nullable = false)
    lateinit open var currentToken: UUID

    @Column(name = "profile_pic", nullable = true)
    open var profilePic: ByteArray? = null

    @OneToMany(mappedBy = "user")
    lateinit open var memberships: MutableList<Member>
        protected set

    @OneToMany(mappedBy = "createdBy")
    lateinit open var createdTickets: MutableList<Ticket>
        protected set

    @OneToMany(mappedBy = "user")
    lateinit open var comments: MutableList<Comment>
        protected set

    @OneToMany(mappedBy = "user")
    lateinit open var assignedTicketUsers: MutableList<AssignedTicketUser>
        protected set

    @OneToMany(mappedBy = "user")
    lateinit open var events: MutableList<TicketEvent>
        protected set

    @OneToMany(mappedBy = "addedUser")
    lateinit open var userAddedEvents: MutableList<TicketEventUserAdded>
        protected set

    @OneToMany(mappedBy = "removedUser")
    lateinit open var userRemovedEvents: MutableList<TicketEventUserRemoved>
        protected set
}
