package io.ticktag.persistence.user.entity

import io.ticktag.persistence.member.entity.Member
import io.ticktag.persistence.ticket.entity.*
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
open class User protected constructor() {
    companion object {
        fun create(mail: String, passwordHash: String, name: String, username: String, role: Role, currentToken: UUID): User {
            return createWithId(UUID.randomUUID(), mail, passwordHash, name, username, role, currentToken)
        }

        fun createWithId(uuid: UUID, mail: String, passwordHash: String, name: String, username: String, role: Role,
                         currentToken: UUID): User {
            val u = User()
            u.id = uuid
            u.mail = mail
            u.passwordHash = passwordHash
            u.name = name
            u.username = username
            u.role = role
            u.currentToken = currentToken
            u.memberships = mutableListOf()
            u.createdTickets = mutableListOf()
            u.comments = mutableListOf()
            u.assignedTicketUsers = mutableListOf()
            u.events = mutableListOf()
            u.userAddedEvents = mutableListOf()
            u.userRemovedEvents = mutableListOf()
            u.disabled = false
            return u
        }

        const val USERNAME_REGEX = "^[a-z0-9_]{3,30}$"
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

    @Column(name = "username", nullable = false)
    lateinit open var username: String

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit open var role: Role

    @Column(name = "current_token", nullable = false)
    lateinit open var currentToken: UUID

    @Column(name = "disabled", nullable = false)
    open var disabled: Boolean = false


    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = true)
    open var image: UserImage? = null

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