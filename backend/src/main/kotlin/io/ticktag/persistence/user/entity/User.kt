package io.ticktag.persistence.user.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
open class User {
    companion object {
        fun create(mail: String, passwordHash: String, name: String, role: Role, currentToken: UUID): User {
            val u = User()
            u.id = UUID.randomUUID()
            u.mail = mail
            u.passwordHash = passwordHash
            u.name = name
            u.role = role
            u.currentToken = currentToken
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

    protected constructor()
}
