package io.ticktag.persistence.user.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
open class User {
    @Id
    @Column(name = "id")
    open var id: UUID = UUID.randomUUID()

    @Column(name = "mail", nullable = false)
    open var mail: String = ""

    @Column(name = "password_hash", nullable = false)
    open var passwordHash: String = ""

    @Column(name = "name", nullable = false)
    open var name: String = ""

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    open var role: Role = Role.USER

    @Column(name = "current_token", nullable = false)
    open var currentToken: UUID = UUID.randomUUID()
}
